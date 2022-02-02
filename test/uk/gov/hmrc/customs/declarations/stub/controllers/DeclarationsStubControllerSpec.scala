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

package uk.gov.hmrc.customs.declarations.stub.controllers

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.ContentTypes
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{bind, Injector}
import play.api.mvc.Codec
import play.api.test.FakeRequest
import play.api.test.Helpers._
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.customs.declarations.stub.config.AppConfig
import uk.gov.hmrc.customs.declarations.stub.connector.NotificationConnector
import uk.gov.hmrc.customs.declarations.stub.repositories.{Client, ClientRepository}
import uk.gov.hmrc.customs.declarations.stub.testdata.xmls.SubmissionRequests._
import uk.gov.hmrc.http.HeaderCarrier

class DeclarationsStubControllerSpec
    extends AnyWordSpec with Matchers with GuiceOneAppPerSuite with MockitoSugar with ScalaFutures
    with BeforeAndAfterEach {

  val submissionUri = "/"
  val cancellationUri = "/cancellation-requests"
  val submissionNoNotificationUri = "/customs-declare-imports/"
  val cancellationNoNotificationUri = "/customs-declare-imports/cancellation-requests"

  def buildFakeRequest(xmlBody: String, method: String, uriVal: String): FakeRequest[String] =
    FakeRequest(method, uriVal)
      .withBody(xmlBody)
      .withHeaders(
        AUTHORIZATION -> "dummyToken",
        CONTENT_TYPE -> ContentTypes.XML(Codec.utf_8),
        ACCEPT -> "application/vnd.hmrc.1.0+xml",
        "X-Client-ID" -> "CLIENT ID"
      )

  val fakeSubmissionXmlRequest: FakeRequest[String] =
    buildFakeRequest(validSubmissionXml.toString, "POST", submissionUri)

  val fakeCancellationXmlRequest: FakeRequest[String] =
    buildFakeRequest(validCancellationXml.toString, "POST", cancellationUri)

  val fakeSubmissionNoNotificationXmlRequest: FakeRequest[String] =
    buildFakeRequest(validSubmissionXml.toString, "POST", submissionNoNotificationUri)

  val fakeCancellationNoNotificationXmlRequest: FakeRequest[String] =
    buildFakeRequest(validCancellationXml.toString, "POST", cancellationNoNotificationUri)

  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val mockClientRepo: ClientRepository = mock[ClientRepository]
  val mockNotificationConnector: NotificationConnector = mock[NotificationConnector]
  val mockAppConfig: AppConfig = mock[AppConfig]

  def injector: Injector = app.injector

  protected def component[T: ClassTag]: T = app.injector.instanceOf[T]

  def appConfig: AppConfig = injector.instanceOf[AppConfig]

  override lazy val app: Application =
    GuiceApplicationBuilder()
      .overrides(
        bind[AuthConnector].to(mockAuthConnector),
        bind[ClientRepository].to(mockClientRepo),
        bind[NotificationConnector].to(mockNotificationConnector),
        bind[AppConfig].to(mockAppConfig)
      )
      .build()

  override def beforeEach() {
    reset(mockClientRepo, mockAuthConnector, mockNotificationConnector)
  }

  val client = Client("clientId", "callbackUrl", "token", BSONObjectID.generate())

  "DeclarationStubController" should {

    "Standard Endpoints" should {
      "return ACCEPTED and send notification when submission endpoint called " in {
        when(
          mockAuthConnector.authorise(any[Predicate], any[Retrieval[Unit]])(any[HeaderCarrier], any[ExecutionContext])
        ).thenReturn(Future.successful(()))
        when(mockClientRepo.findByClientId(any())).thenReturn(Future.successful(Some(client)))

        val result = route(app, fakeSubmissionXmlRequest).get

        status(result) should be(ACCEPTED)
        verify(mockNotificationConnector, times(1)).notifyInDueCourse(any(), any(), any(), any(), any())
      }

      "return BAD_REQUEST when invalidxml is sent to submission Endpoint" in {
        when(
          mockAuthConnector.authorise(any[Predicate], any[Retrieval[Unit]])(any[HeaderCarrier], any[ExecutionContext])
        ).thenReturn(Future.successful(()))
        when(mockClientRepo.findByClientId(any())).thenReturn(Future.successful(Some(client)))

        val result = route(app, fakeSubmissionXmlRequest.withBody("<some></some>")).get

        status(result) should be(BAD_REQUEST)
        verifyNoMoreInteractions(mockNotificationConnector)
      }

      "return ACCEPTED and send notification when cancellation endpoint called " in {
        when(
          mockAuthConnector.authorise(any[Predicate], any[Retrieval[Unit]])(any[HeaderCarrier], any[ExecutionContext])
        ).thenReturn(Future.successful(()))
        when(mockClientRepo.findByClientId(any())).thenReturn(Future.successful(Some(client)))

        val result = route(app, fakeCancellationXmlRequest).get

        status(result) should be(ACCEPTED)
        verify(mockNotificationConnector, times(1)).notifyInDueCourse(any(), any(), any(), any(), any())
      }

      "return BAD_REQUEST when invalidxml is sent to cancellation Endpoint" in {
        when(
          mockAuthConnector.authorise(any[Predicate], any[Retrieval[Unit]])(any[HeaderCarrier], any[ExecutionContext])
        ).thenReturn(Future.successful(()))
        when(mockClientRepo.findByClientId(any())).thenReturn(Future.successful(Some(client)))

        val result = route(app, fakeCancellationXmlRequest.withBody("<some></some>")).get

        status(result) should be(BAD_REQUEST)
        verifyNoMoreInteractions(mockNotificationConnector)
      }

    }

    "No Notification Endpoints" should {
      "return ACCEPTED, not send notification when submissionNoNotification endpoint called " in {
        when(
          mockAuthConnector.authorise(any[Predicate], any[Retrieval[Unit]])(any[HeaderCarrier], any[ExecutionContext])
        ).thenReturn(Future.successful(()))
        when(mockClientRepo.findByClientId(any())).thenReturn(Future.successful(Some(client)))

        val result = route(app, fakeSubmissionNoNotificationXmlRequest).get

        status(result) should be(ACCEPTED)
        verifyNoMoreInteractions(mockNotificationConnector)
      }

      "return BADREQUEST when invalidxml is sent to submission Endpoint" in {
        when(
          mockAuthConnector.authorise(any[Predicate], any[Retrieval[Unit]])(any[HeaderCarrier], any[ExecutionContext])
        ).thenReturn(Future.successful(()))
        when(mockClientRepo.findByClientId(any())).thenReturn(Future.successful(Some(client)))

        val result = route(app, fakeSubmissionNoNotificationXmlRequest.withBody("<some></some>")).get

        status(result) should be(BAD_REQUEST)
        verifyNoMoreInteractions(mockNotificationConnector)
      }

      "return ACCEPTED, not send notification when cancellationNoNotification endpoint called " in {
        when(
          mockAuthConnector.authorise(any[Predicate], any[Retrieval[Unit]])(any[HeaderCarrier], any[ExecutionContext])
        ).thenReturn(Future.successful(()))
        when(mockClientRepo.findByClientId(any())).thenReturn(Future.successful(Some(client)))

        val result = route(app, fakeCancellationNoNotificationXmlRequest).get

        status(result) should be(ACCEPTED)
        verifyNoMoreInteractions(mockNotificationConnector)
      }

      "return BADREQUEST when invalidxml is sent to cancellation Endpoint" in {
        when(
          mockAuthConnector.authorise(any[Predicate], any[Retrieval[Unit]])(any[HeaderCarrier], any[ExecutionContext])
        ).thenReturn(Future.successful(()))
        when(mockClientRepo.findByClientId(any())).thenReturn(Future.successful(Some(client)))

        val result = route(app, fakeCancellationNoNotificationXmlRequest.withBody("<some></some>")).get

        status(result) should be(BAD_REQUEST)
        verifyNoMoreInteractions(mockNotificationConnector)
      }

    }
  }

}
