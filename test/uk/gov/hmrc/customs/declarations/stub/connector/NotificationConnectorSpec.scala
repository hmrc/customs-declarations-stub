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

import java.lang.Thread
import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatest.mockito.MockitoSugar
import org.mockito.ArgumentCaptor
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.{any, anyString, eq => meq}
import play.api.mvc.Results._
import play.api.http.Status.ACCEPTED
import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.customs.declarations.stub.config.AppConfig
import uk.gov.hmrc.customs.declarations.stub.models.ApiHeaders
import uk.gov.hmrc.customs.declarations.stub.repositories.{Client, Notification, NotificationRepository}
import uk.gov.hmrc.customs.declarations.stub.utils.XmlPayloads
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.wco.dec.MetaData

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration

class NotificationConnectorSpec extends UnitSpec with MockitoSugar with ScalaFutures{

  val mockHttpClient: HttpClient = mock[HttpClient]

  trait SetUp {

    implicit val mockAppConfig: AppConfig = mock[AppConfig]
    val mockNotificationRepository = mock[NotificationRepository]
    val mockNotificationValueGenerator = mock[NotificationValueGenerator]
    implicit val system: ActorSystem = ActorSystem("test")

    val testObj = new NotificationConnector(mockHttpClient,
                                            mockNotificationRepository,
                                            mockNotificationValueGenerator)
    reset(mockHttpClient, mockNotificationRepository, mockNotificationValueGenerator)
  }

  "NotificationConnector" should {
    "return Accepted ands send the notification body from the repo" in new SetUp{
      val client = Client("clientId", "callBackUrl", "token")
      val apiHeaders = ApiHeaders("Accept", "contentType", "clientId", badgeId = None)
      val metaData: MetaData = mock[MetaData]
      val xmlBody = "<xml></xml>"

      returnResponseForRequest(Future.successful(mock[HttpResponse]))
      when(mockNotificationRepository.findByClientAndOperationAndMetaData(any(), any(), any()))
        .thenReturn(Future.successful(Some(Notification("clientId", "operation", "lrn", xmlBody))))
      val conversationId: String = UUID.randomUUID().toString
      val result: Unit = await(testObj.notifyInDueCourse("operation", apiHeaders, client, metaData, new FiniteDuration(500, TimeUnit.MILLISECONDS), conversationId))
       Thread.sleep(850)
        result shouldBe((): Unit)
        verify(mockNotificationRepository, times(1))
          .findByClientAndOperationAndMetaData(any(), any(), any())

      verify(mockHttpClient, times(1)).POSTString(any(), meq(xmlBody), any())(any(), any(), any())
    }

    "return Accepted and call use the default notification Body" in new SetUp{
      val client = Client("clientId", "callBackUrl", "token")
      val apiHeaders = ApiHeaders("Accept", "contentType", "clientId", badgeId = None)
      val metaData: MetaData = mock[MetaData]
      val generatedMrn = "MRN7878787"

      returnResponseForRequest(Future.successful(mock[HttpResponse]))
      when(mockNotificationRepository.findByClientAndOperationAndMetaData(any(), any(), any()))
        .thenReturn(Future.successful(None))
      when(mockNotificationValueGenerator.generateMRN).thenReturn(generatedMrn)
      val conversationId: String = UUID.randomUUID().toString
      val result: Unit = await(testObj.notifyInDueCourse("operation", apiHeaders, client, metaData, new FiniteDuration(500, TimeUnit.MILLISECONDS), conversationId))
      Thread.sleep(750)
      result shouldBe((): Unit)
      verify(mockNotificationRepository, times(1))
        .findByClientAndOperationAndMetaData(any(), any(), any())

      verify(mockHttpClient, times(1)).POSTString(any(), meq(XmlPayloads.acceptedImportNotification(generatedMrn).toString), any())(any(), any(), any())
    }
  }

  private def returnResponseForRequest(eventualResponse: Future[HttpResponse]) = {
    when(mockHttpClient.POSTString(anyString, anyString, any[Seq[(String, String)]])(
      any[HttpReads[HttpResponse]](), any[HeaderCarrier](), any[ExecutionContext]))
      .thenReturn(eventualResponse)
  }
}
