/*
 * Copyright 2019 HM Revenue & Customs
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

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.http.{ContentTypes, HeaderNames}
import uk.gov.hmrc.customs.declarations.stub.config.AppConfig
import uk.gov.hmrc.customs.declarations.stub.models.ApiHeaders
import uk.gov.hmrc.customs.declarations.stub.repositories.{Client, NotificationRepository}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.wco.dec.MetaData

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source


@Singleton
class NotificationConnector @Inject()( http: HttpClient,
                                       notificationRepo: NotificationRepository)
                                     (implicit val appConfig: AppConfig,
                                      ec: ExecutionContext,
                                      actorSystem: ActorSystem){

  private val defaultNotification =
    Source.fromInputStream(
      getClass.getResourceAsStream("/messages/example_accepted_import_notification.xml")
    ).getLines().mkString

  def notifyInDueCourse(operation: String,
                        headers: ApiHeaders,
                        client: Client,
                        meta: MetaData,
                        duration: FiniteDuration =  new FiniteDuration(2, TimeUnit.SECONDS),
                        conversationId: String): Unit = {
    scheduleEachOnce(operation, headers, conversationId, client,  meta, duration)
  }

  def scheduleEachOnce(operation: String,
                       headers: ApiHeaders,
                       conversationId: String,
                       client: Client,
                       meta: MetaData,
                       duration: FiniteDuration): Unit =

    actorSystem.scheduler.scheduleOnce(duration) {
      notificationRepo.findByClientAndOperationAndMetaData(client.clientId, operation, meta).map { maybeNotification =>
        Logger.info("Entering async request notification")
        val xml = maybeNotification.map(_.xml).getOrElse(defaultNotification)
        Logger.debug(s"scheduling one notification call ${xml.toString}")
        sendNotificationWithDelay(client, conversationId, xml).onComplete { _ =>
          Logger.info("Exiting async request notification")
        }
      }
    }

  private def sendNotificationWithDelay(client: Client, conversationId: String, xml: String, delay: Duration = 5.seconds)
                                       (implicit rds: HttpReads[HttpResponse], ec: ExecutionContext): Future[HttpResponse] = {
    http.POSTString(
      client.callbackUrl,
      xml,
      Seq(
        HeaderNames.AUTHORIZATION -> s"Bearer ${client.token}",
        HeaderNames.CONTENT_TYPE -> ContentTypes.XML,
        "X-Conversation-ID" -> conversationId
      )
    )(rds, HeaderCarrier(), ec)
  }
}
