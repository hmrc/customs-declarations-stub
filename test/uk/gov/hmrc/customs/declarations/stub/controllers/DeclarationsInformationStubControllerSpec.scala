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

import org.mockito.ArgumentMatchers.{anyString, eq => eqTo}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.BeforeAndAfterEach
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scala.xml.XML
import uk.gov.hmrc.customs.declarations.stub.base.AuthConnectorMock
import uk.gov.hmrc.customs.declarations.stub.models.declarationstatus.DeclarationStatusResponse._
import uk.gov.hmrc.customs.declarations.stub.services.DeclarationStatusResponseBuilder
import uk.gov.hmrc.customs.declarations.stub.testdata.CommonTestData.{eori, mrn, signedInUser}

class DeclarationsInformationStubControllerSpec
    extends AnyWordSpec with Matchers with MockitoSugar with ScalaFutures with BeforeAndAfterEach
    with AuthConnectorMock {

  private val declarationsInformationStubService = mock[DeclarationStatusResponseBuilder]

  private val controller = new DeclarationsInformationStubController(
    stubControllerComponents(),
    authActionMock,
    declarationsInformationStubService
  )

  private val request = FakeRequest()
  private val successfulResponse = SuccessfulResponse(<testResponse/>)

  override def beforeEach(): Unit = {
    super.beforeEach()

    reset(declarationsInformationStubService)
    authorizedUser(signedInUser(eori))
    when(declarationsInformationStubService.buildDeclarationStatus(anyString(), anyString()))
      .thenReturn(successfulResponse)
  }

  override def afterEach(): Unit = {
    reset(declarationsInformationStubService)
    super.afterEach()
  }

  "DeclarationsInformationStubController on getDeclarationStatus" when {

    "user is not authorized" should {

      "return Unauthorized response" in {

        userWithoutEori()

        val result = controller.getDeclarationStatus(mrn)(request)
        status(result) mustBe UNAUTHORIZED

      }
    }

    "user is authorized" should {

      "call DeclarationsInformationStubService" in {

        controller.getDeclarationStatus(mrn)(request).futureValue

        verify(declarationsInformationStubService).buildDeclarationStatus(eqTo(eori), eqTo(mrn))
      }
    }

    "user is authorized" when {

      "DeclarationsInformationStubService returns Successful response" should {

        "return Ok (200) with that response" in {

          val result = controller.getDeclarationStatus(mrn)(request)

          status(result) mustBe OK
          XML.loadString(contentAsString(result)) mustBe successfulResponse.body
        }
      }

      "DeclarationsInformationStubService returns NotFound response" should {

        "return NotFound (404) response" in {

          when(declarationsInformationStubService.buildDeclarationStatus(anyString(), anyString()))
            .thenReturn(NotFoundResponse)

          val result = controller.getDeclarationStatus(mrn)(request)

          status(result) mustBe NOT_FOUND
          XML.loadString(contentAsString(result)) mustBe NotFoundResponse.body
        }
      }
    }
  }

}
