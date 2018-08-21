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

import model.{Header, PushNotificationRequestBody, DeclarantCallbackData, PushNotificationRequest}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.mockito.{ArgumentCaptor, ArgumentMatchers}
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.Writes
import uk.gov.hmrc.customs.api.common.config.{ServiceConfig, ServiceConfigProvider}
import uk.gov.hmrc.customs.test.CustomsPlaySpec
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}
import scala.xml.NodeSeq
import TestData._

class PushNotificationServiceConnectorSpec extends CustomsPlaySpec with MockitoSugar {

  private val mockHttpClient = mock[HttpClient]
  private val serviceConfigProvider = mock[ServiceConfigProvider]

  private val connector = new PushNotificationServiceConnector(
    mockHttpClient,
    serviceConfigProvider
  )
  private val url = "the-url"

  private val emulatedHttpVerbsException = new RuntimeException("FooBar")

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  "PushNotificationServiceConnector" should {
    when(serviceConfigProvider.getConfig("public-notification")).thenReturn(ServiceConfig(url, None, "default"))

    "POST valid payload" in {
      when(mockHttpClient.POST(any[String](), any[NodeSeq](), any[Seq[(String,String)]]())(
        any[Writes[NodeSeq]](), any[HttpReads[HttpResponse]](), any[HeaderCarrier](), any[ExecutionContext]()))
        .thenReturn(Future.successful(mock[HttpResponse]))

      connector.send(pushNotificationRequest).futureValue

      val requestBody = ArgumentCaptor.forClass(classOf[PushNotificationRequestBody])
      verify(mockHttpClient).POST(ArgumentMatchers.eq(url), requestBody.capture(), any[Seq[(String,String)]]())(
        any[Writes[PushNotificationRequestBody]](), any[HttpReads[HttpResponse]](), any[HeaderCarrier](), any[ExecutionContext]())
      val body = requestBody.getValue.asInstanceOf[PushNotificationRequestBody]
      body must be (pushNotificationRequest.body)
    }

    "propagate exception in HTTP VERBS post" in {
      when(mockHttpClient.POST(any[String](), any[NodeSeq](), any[Seq[(String,String)]]())(
        any[Writes[NodeSeq]](), any[HttpReads[HttpResponse]](), any[HeaderCarrier](), any[ExecutionContext]()))
        .thenThrow(emulatedHttpVerbsException)

      val caught = intercept[RuntimeException] {
        connector.send(pushNotificationRequest).futureValue
      }

      caught must be (emulatedHttpVerbsException)
    }
  }
}
