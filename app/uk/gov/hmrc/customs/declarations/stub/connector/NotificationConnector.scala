/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.customs.declarations.stub.connector

import java.util.{Timer, TimerTask}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, _}
import scala.util.Failure

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.http.{ContentTypes, HeaderNames}
import uk.gov.hmrc.customs.declarations.stub.config.AppConfig
import uk.gov.hmrc.customs.declarations.stub.generators.NotificationGenerator
import uk.gov.hmrc.customs.declarations.stub.generators.NotificationGenerator._
import uk.gov.hmrc.customs.declarations.stub.repositories.{Client, NotificationRepository}
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}
import uk.gov.hmrc.wco.dec.MetaData

@Singleton
class NotificationConnector @Inject()(
  http: HttpClient,
  notificationRepo: NotificationRepository,
  generator: NotificationGenerator
)(implicit val appConfig: AppConfig, ec: ExecutionContext, actorSystem: ActorSystem) {

  private val logger = Logger(this.getClass)

  def notifyInDueCourse(
    operation: String,
    client: Client,
    meta: MetaData,
    duration: FiniteDuration = Duration(2, "secs"),
    conversationId: String
  ): Unit = scheduleEachOnce(operation, conversationId, client, meta, duration)

  private val defaultDelay = 0.seconds

  private def scheduleEachOnce(
    operation: String,
    conversationId: String,
    client: Client,
    meta: MetaData,
    duration: FiniteDuration
  ): Unit =
    actorSystem.scheduler.scheduleOnce(duration) {
      notificationRepo
        .findByClientAndOperationAndMetaData(client.clientId, operation, meta)
        .map { maybeNotification =>
          logger.info("Entering async request notification")

          val (delay, xml) = maybeNotification.map { notification =>
            (defaultDelay, notification.xml)
          }.getOrElse {
            logger.info("Notification not found in database - generate one dynamically")
            lazy val default = generator.generateAcceptNotificationWithRandomMRN().toString
            meta.declaration.fold((defaultDelay, default)) { declaration =>
              declaration.functionalReferenceId.fold((defaultDelay, default)) { lrn =>
                logger.info(s"Dynamic generating for LNR $lrn ")
                generate(lrn, default)
              }
            }
          }

          logger.debug(s"scheduling one notification call ${xml}")
          sendNotificationWithDelay(client, conversationId, xml, delay)
        }
        .andThen {
          case Failure(e) => logger.error("Problem on sending notification back", e)
        }
    }

  private def generate(lrn: String, default: String): (FiniteDuration, String) = {
    val delay = extractDelay(lrn)
    lrn.headOption match {
      case Some('B') => (delay, generator.generate(lrn, Seq(Rejected)).toString)
      case Some('D') => (delay, generator.generate(lrn, Seq(Accepted, AdditionalDocumentsRequired)).toString)
      case Some('G') => (delay, generator.generate(lrn, Seq(Accepted)).toString)
      case Some('Q') => (delay, generator.generate(lrn, Seq(QueryNotificationMessage)).toString)
      case Some('R') => (delay, generator.generate(lrn, Seq(Received)).toString)
      case Some('U') => (delay, generator.generate(lrn, Seq(UndergoingPhysicalCheck)).toString)
      case Some('X') => (delay, generator.generate(lrn, Seq(GoodsHaveExitedTheCommunity)).toString)
      case _         => (delay, importsSpecificErrors(lrn, default))
    }
  }

  private val onDelay = "([BDGQRUX])([0-9])".r

  private def extractDelay(lrn: String): FiniteDuration =
    lrn.take(2) match {
      case onDelay(_, delay) => Duration(delay.toLong, "secs")
      case _                 => defaultDelay
    }

  private def importsSpecificErrors(lrn: String, default: String): String =
    lrn match {
      case lrnForTaxLiability if lrnForTaxLiability.startsWith("TAX_LIABILITY") =>
        generator.generate(lrnForTaxLiability, Seq(NotificationGenerator.taxLiability)).toString

      case lrnForBalance if lrnForBalance.startsWith("INSUFFICIENT") =>
        generator.generate(lrnForBalance, Seq(NotificationGenerator.insufficientBalanceInDan)).toString

      case lrnForBalanceReminder if lrnForBalanceReminder.startsWith("REMINDER") =>
        generator.generate(lrnForBalanceReminder, Seq(NotificationGenerator.insufficientBalanceInDanReminder)).toString

      case _ => default
    }

  private def sendNotificationWithDelay(client: Client, conversationId: String, xml: String, delay: Duration)(
    implicit rds: HttpReads[HttpResponse],
    ec: ExecutionContext
  ): Unit =
    (new Timer).schedule(
      new TimerTask() {
        val payload = Seq(
          HeaderNames.AUTHORIZATION -> s"Bearer ${client.token}",
          HeaderNames.CONTENT_TYPE -> ContentTypes.XML,
          "X-Conversation-ID" -> conversationId
        )

        def run: Unit =
          http
            .POSTString(client.callbackUrl, xml, payload)(rds, HeaderCarrier(), ec)
            .onComplete(_ => logger.info("Exiting async request notification"))

      },
      delay.toMillis
    )
}
