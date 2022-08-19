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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import play.api.http.ContentTypes
import play.api.mvc.Codec
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.customs.declarations.stub.base.IntegrationTestSpec
import uk.gov.hmrc.customs.declarations.stub.testdata.xmls.SubmissionRequests._

class DeclarationsStubControllerISpec extends IntegrationTestSpec {

  val submissionUri = "/"
  val cancellationUri = "/cancellation-requests"

  val clientId = "customs-declare-exports"

  def buildFakeRequest(xmlBody: String, method: String, uriVal: String): FakeRequest[String] =
    FakeRequest(method, uriVal)
      .withBody(xmlBody)
      .withHeaders(
        AUTHORIZATION -> "Some token",
        CONTENT_TYPE -> ContentTypes.XML(Codec.utf_8),
        ACCEPT -> "application/vnd.hmrc.1.0+xml",
        "X-Client-ID" -> clientId
      )

  val fakeSubmissionXmlRequest: FakeRequest[String] =
    buildFakeRequest(validSubmissionXml.toString, "POST", submissionUri)

  val fakeCancellationXmlRequest: FakeRequest[String] =
    buildFakeRequest(validCancellationXml.toString, "POST", cancellationUri)

  override def beforeEach(): Unit = {
    super.beforeEach()
    authorizedUser
  }

  "DeclarationStubController" should {

    "Standard Endpoints" should {
      "return ACCEPTED and send notification when submission endpoint called " in {
        val result = route(app, fakeSubmissionXmlRequest).get

        status(result) should be(ACCEPTED)
        verify(notificationConnectorMock, times(1)).notifyInDueCourse(any(), any(), any(), any(), any(), any())
      }

      "return BAD_REQUEST when invalidxml is sent to submission Endpoint" in {
        val result = route(app, fakeSubmissionXmlRequest.withBody("<some></some>")).get

        status(result) should be(BAD_REQUEST)
        verifyNoMoreInteractions(notificationConnectorMock)
      }

      "return ACCEPTED and send notification when cancellation endpoint called " in {
        val result = route(app, fakeCancellationXmlRequest).get

        status(result) should be(ACCEPTED)
        verify(notificationConnectorMock, times(1)).notifyInDueCourse(any(), any(), any(), any(), any(), any())
      }

      "return BAD_REQUEST when invalidxml is sent to cancellation Endpoint" in {
        val result = route(app, fakeCancellationXmlRequest.withBody("<some></some>")).get

        status(result) should be(BAD_REQUEST)
        verifyNoMoreInteractions(notificationConnectorMock)
      }
    }
  }
}
