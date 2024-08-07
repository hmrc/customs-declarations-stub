/*
 * Copyright 2024 HM Revenue & Customs
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

import org.apache.pekko.actor.ActorSystem
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.MockitoSugar.{mock, reset, times, verify, when}
import org.mockito.stubbing.ScalaOngoingStubbing
import uk.gov.hmrc.customs.declarations.stub.base.UnitTestSpec
import uk.gov.hmrc.customs.declarations.stub.config.AppConfig
import uk.gov.hmrc.customs.declarations.stub.generators.{NotificationGenerator, NotificationValueGenerator}
import uk.gov.hmrc.customs.declarations.stub.models.Client
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}
import uk.gov.hmrc.wco.dec.{Declaration, MetaData}

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.Source

class NotificationConnectorSpec extends UnitTestSpec {

  val mockHttpClient: HttpClient = mock[HttpClient]

  trait SetUp {
    implicit val mockAppConfig: AppConfig = mock[AppConfig]
    val mockNotificationGenerator = new NotificationGenerator(new NotificationValueGenerator())
    implicit val system: ActorSystem = ActorSystem("test")

    val testObj = new NotificationConnector(mockHttpClient, mockNotificationGenerator)
    reset(mockHttpClient)
  }

  "NotificationConnector" should {

    "return Accepted and call use the default notification Body" in new SetUp {
      val client = Client("clientId", "callBackUrl", "token")
      val metaData: MetaData = MetaData(declaration = Some(Declaration(functionalReferenceId = Some("BLRN"), typeCode = Some("ABC"))))

      returnResponseForRequest(Future.successful(mock[HttpResponse]))

      val conversationId = UUID.randomUUID.toString
      val result = testObj.notifyInDueCourse("submit", conversationId, None, client, metaData, Duration(500, "ms"))

      Thread.sleep(2000)
      result shouldBe ((): Unit)

      val payloadCaptor = ArgumentCaptor.forClass(classOf[String])
      verify(mockHttpClient, times(1)).POSTString(any(), payloadCaptor.capture(), any())(any(), any(), any())
      scala.xml.XML.load(Source.fromString(payloadCaptor.getValue))
    }
  }

  private def returnResponseForRequest(eventualResponse: Future[HttpResponse]): ScalaOngoingStubbing[Future[HttpResponse]] =
    when(
      mockHttpClient
        .POSTString(anyString(), anyString(), any[Seq[(String, String)]])(any[HttpReads[HttpResponse]](), any[HeaderCarrier](), any[ExecutionContext])
    ).thenReturn(eventualResponse)
}
