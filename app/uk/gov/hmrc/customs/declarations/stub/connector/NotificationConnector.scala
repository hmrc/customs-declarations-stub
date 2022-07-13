/*
 * Copyright 2022 HM Revenue & Customs
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
import scala.util.{Failure, Random}
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

import java.time.{ZonedDateTime, ZoneId}

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
                generate(lrn, default, operation, declaration.id)
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

  private def defineMrn(maybeMrn: Option[String]) = {
    maybeMrn.getOrElse{
      val year = ZonedDateTime.now(ZoneId.of("Europe/London")).getYear % 100
      val country = "GB"
      val random = new Random(maybeMrn.hashCode)
      val code = random.nextInt(8999) + 1000
      val charSection = random.shuffle("ABCDEFGHIJKLMNOPQRSTUVWXYZ".toSeq).take(2).mkString
      val secondCode = random.nextInt(8999999) + 1000000
      val witchoutCheck = s"${year}${country}${code}${charSection}${secondCode}"
      val check = witchoutCheck.zipWithIndex.foldLeft(0) {
        case (input, (char, index)) => input + (NotificationGenerator.characterValue(char) * (1 << index))
      }
      val controlDigit = ((check % 11) % 10).toString
      witchoutCheck + controlDigit
    }
  }

  private def generate(lrn: String, default: String, operation: String, maybeMrn: Option[String]): (FiniteDuration, String) = {
    val delay = extractDelay(lrn)
    val mrn = defineMrn(maybeMrn)

    if (operation == "cancel") {
      lrn.charAt(2) match {
        case 'S' => (delay, generator.generate(lrn, mrn, Seq(CustomsPositionGranted)))
        case 'D' => (delay, generator.generate(lrn, mrn, Seq(CustomsPositionDenied)))
        case _   => (delay, generator.generate(lrn, mrn, Seq(QueryNotificationMessage)))
      }
    } else {
      lrn.headOption match {
        case Some('B') => (delay, generator.generate(lrn, mrn, List(Rejected)))
        case Some('C') => (delay, generator.generate(lrn, mrn, List(Accepted, Cleared)))
        case Some('D') => (delay, generator.generate(lrn, mrn, List(Accepted, AdditionalDocumentsRequired)))
        case Some('G') => (delay, generator.generate(lrn, mrn, List(Accepted)))
        case Some('Q') => (delay, generator.generate(lrn, mrn, List(QueryNotificationMessage)))
        case Some('R') => (delay, generator.generate(lrn, mrn, List(Received)))
        case Some('U') => (delay, generator.generate(lrn, mrn, List(UndergoingPhysicalCheck)))
        case Some('X') => (delay, generator.generate(lrn, mrn, List(GoodsHaveExitedTheCommunity)))
        case _         => (delay, importsSpecificErrors(lrn, mrn, default))
      }
    }
  }

  private val onDelay = "([BDGQRUX])([0-9])".r

  private def extractDelay(lrn: String): FiniteDuration =
    lrn.take(2) match {
      case onDelay(_, delay) => Duration(delay.toLong, "secs")
      case _                 => defaultDelay
    }

  private def importsSpecificErrors(lrn: String, mrn: String, default: String): String =
    lrn match {
      case lrnForTaxLiability if lrnForTaxLiability.startsWith("TAX_LIABILITY") =>
        generator.generate(lrnForTaxLiability, mrn, List(NotificationGenerator.taxLiability))

      case lrnForBalance if lrnForBalance.startsWith("INSUFFICIENT") =>
        generator.generate(lrnForBalance, mrn, List(NotificationGenerator.insufficientBalanceInDan))

      case lrnForBalanceReminder if lrnForBalanceReminder.startsWith("REMINDER") =>
        generator.generate(lrnForBalanceReminder, mrn, List(NotificationGenerator.insufficientBalanceInDanReminder))

      case _ => default
    }

  private def sendNotificationWithDelay(client: Client, conversationId: String, xml: String, delay: Duration)(
    implicit rds: HttpReads[HttpResponse],
    ec: ExecutionContext
  ): Unit =
    (new Timer).schedule(
      new TimerTask() {
        val payload = List(
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
