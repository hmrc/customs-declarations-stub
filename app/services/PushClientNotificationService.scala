/*
 * Copyright 2018 HM Revenue & Customs
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

package services

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}

import model.{PushNotificationRequestBody, PushNotificationRequest, ClientNotification, DeclarantCallbackData}
import play.api.Logger
import play.api.http.HeaderNames._
import play.api.http.MimeTypes
import uk.gov.hmrc.customs.api.common.config.ServiceConfigProvider
import uk.gov.hmrc.http.{HttpException, HttpResponse, HeaderCarrier}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class PushClientNotificationService @Inject() (pushNotificationServiceConnector: PushNotificationServiceConnector) {

  private implicit val hc = HeaderCarrier()

  def send(declarantCallbackData: DeclarantCallbackData, clientNotification: ClientNotification): Boolean = {

    val pushNotificationRequest = pushNotificationRequestFrom(declarantCallbackData, clientNotification)

    val result = scala.concurrent.blocking {
      Await.ready(pushNotificationServiceConnector.send(pushNotificationRequest), Duration.apply(25, TimeUnit.SECONDS)).value.get.isSuccess
    }
    if (result) {
      Logger.debug("Notification has been pushed")
    } else {
      Logger.error("Notification push failed")
    }
    result
  }

  private def pushNotificationRequestFrom(declarantCallbackData: DeclarantCallbackData,
                                            clientNotification: ClientNotification): PushNotificationRequest = {

    PushNotificationRequest(
      clientNotification.csid.id.toString,
      PushNotificationRequestBody(
        declarantCallbackData.callbackUrl,
        declarantCallbackData.securityToken,
        clientNotification.notification.conversationId.id.toString,
        clientNotification.notification.headers,
        clientNotification.notification.payload
      ))
  }

}
@Singleton
class PushNotificationServiceConnector @Inject()(http: HttpClient,
                                                 serviceConfigProvider: ServiceConfigProvider) {

  private val outboundHeaders = Seq(
    (ACCEPT, MimeTypes.JSON),
    (CONTENT_TYPE, MimeTypes.JSON))

  // TODO: recover on failure to enqueue to notification queue
  def send(pushNotificationRequest: PushNotificationRequest): Future[Unit] = {
    doSend(pushNotificationRequest) map (_ => () )
  }

  private def doSend(pushNotificationRequest: PushNotificationRequest): Future[HttpResponse] = {
    val url = serviceConfigProvider.getConfig("public-notification").url

    implicit val hc: HeaderCarrier = HeaderCarrier(extraHeaders = outboundHeaders)
    val msg = "Calling push notification service"
    Logger.debug(s"msg is $msg, url is $url, payload = $pushNotificationRequest.body.toString")

    val postFuture = http
      .POST[PushNotificationRequestBody, HttpResponse](url, pushNotificationRequest.body)
      .recoverWith {
        case httpError: HttpException => Future.failed(new RuntimeException(httpError))
      }
      .recoverWith {
        case e: Throwable =>
          Logger.error(s"Call to push notification service failed. POST url=$url, $e")
          Future.failed(e)
      }
    postFuture
  }

}
