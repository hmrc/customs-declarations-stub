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

import java.util.UUID
import java.util.concurrent.TimeUnit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.Source

import akka.actor.ActorSystem
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{any, anyString, eq => meq}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.customs.declarations.stub.config.AppConfig
import uk.gov.hmrc.customs.declarations.stub.generators.{NotificationGenerator, NotificationValueGenerator}
import uk.gov.hmrc.customs.declarations.stub.models.ApiHeaders
import uk.gov.hmrc.customs.declarations.stub.repositories.{Client, Notification, NotificationRepository}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}
import uk.gov.hmrc.wco.dec.{Declaration, MetaData}

class NotificationConnectorSpec extends AnyWordSpec with Matchers with MockitoSugar with ScalaFutures {

  val mockHttpClient: HttpClient = mock[HttpClient]

  trait SetUp {

    implicit val mockAppConfig: AppConfig = mock[AppConfig]
    val mockNotificationRepository = mock[NotificationRepository]
    val mockNotificationGenerator = new NotificationGenerator(new NotificationValueGenerator())
    implicit val system: ActorSystem = ActorSystem("test")

    val testObj = new NotificationConnector(mockHttpClient, mockNotificationRepository, mockNotificationGenerator)
    reset(mockHttpClient, mockNotificationRepository)
  }

  "NotificationConnector" should {
    "return Accepted and send the notification body from the repo" in new SetUp {
      val client = Client("clientId", "callBackUrl", "token")
      val apiHeaders = ApiHeaders("Accept", "contentType", "clientId", badgeId = None)
      val metaData: MetaData = mock[MetaData]
      val xmlBody = "<xml></xml>"

      returnResponseForRequest(Future.successful(mock[HttpResponse]))
      when(mockNotificationRepository.findByClientAndOperationAndMetaData(any(), any(), any()))
        .thenReturn(Future.successful(Some(Notification("clientId", "operation", "lrn", xmlBody))))
      val conversationId: String = UUID.randomUUID().toString
      val result: Unit = testObj.notifyInDueCourse(
        "operation",
        apiHeaders,
        client,
        metaData,
        new FiniteDuration(500, TimeUnit.MILLISECONDS),
        conversationId
      )
      Thread.sleep(2000)
      result shouldBe ((): Unit)
      verify(mockNotificationRepository, times(1))
        .findByClientAndOperationAndMetaData(any(), any(), any())

      verify(mockHttpClient, times(1)).POSTString(any(), meq(xmlBody), any())(any(), any(), any())
    }

    "return Accepted and call use the default notification Body" in new SetUp {
      val client = Client("clientId", "callBackUrl", "token")
      val apiHeaders = ApiHeaders("Accept", "contentType", "clientId", badgeId = None)
      val metaData: MetaData = MetaData(declaration = Some(Declaration(functionalReferenceId = Some("BLRN"))))

      returnResponseForRequest(Future.successful(mock[HttpResponse]))
      when(mockNotificationRepository.findByClientAndOperationAndMetaData(any(), any(), any()))
        .thenReturn(Future.successful(None))
      val conversationId: String = UUID.randomUUID().toString
      val result: Unit = testObj.notifyInDueCourse(
        "operation",
        apiHeaders,
        client,
        metaData,
        new FiniteDuration(500, TimeUnit.MILLISECONDS),
        conversationId
      )
      Thread.sleep(750)
      result shouldBe ((): Unit)
      verify(mockNotificationRepository, times(1))
        .findByClientAndOperationAndMetaData(any(), any(), any())
      val payloadCaptor = ArgumentCaptor.forClass(classOf[String])
      verify(mockHttpClient, times(1)).POSTString(any(), payloadCaptor.capture(), any())(any(), any(), any())
      scala.xml.XML.load(Source.fromString(payloadCaptor.getValue))
    }
  }

  private def returnResponseForRequest(eventualResponse: Future[HttpResponse]) =
    when(
      mockHttpClient.POSTString(anyString, anyString, any[Seq[(String, String)]])(
        any[HttpReads[HttpResponse]](),
        any[HeaderCarrier](),
        any[ExecutionContext]
      )
    ).thenReturn(eventualResponse)
}
