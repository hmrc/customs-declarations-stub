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

package uk.gov.hmrc.customs.declarations.stub.controllers

import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.customs.declarations.stub.base.UnitTestSpec

class CustomsDataStoreStubControllerSpec extends UnitTestSpec {

  private val controller = new CustomsDataStoreStubController(stubControllerComponents())
  private val request = FakeRequest()

  def createThirdPartyEmailVerifiedBody(
                                       eori: String
                                       ): JsValue = {
    Json.obj(
      "eori" -> eori)
  }

  "GET emailIfVerified" should {

    "return a 200(OK) payload with the expected payload for an EORI number not ending in '99' or '98'" in {
      val eori = "GB1234567890"
      val result = controller.emailIfVerified(eori)(request)
      status(result) shouldBe OK
      contentAsString(result) shouldBe controller.verified
    }

    "return a 200(OK) payload with the expected payload for an EORI number ending in '98'" in {
      val eori = "GB1234567898"
      val result = controller.emailIfVerified(eori)(request)
      status(result) shouldBe OK
      contentAsString(result) shouldBe controller.undeliverable
    }

    "return a 404(NOT_FOUND) for an EORI number ending in '99'" in {
      val eori = "GB1234567899"
      val result = controller.emailIfVerified(eori)(request)
      status(result) shouldBe NOT_FOUND
    }
  }

  "POST thirdPartyEmailVerified" should {

    "return a 200(OK) payload with the expected payload for an EORI number not ending in '99' or '98'" in {
      val eori = "GB1234567890"

      val postRequest = FakeRequest("POST", routes.CustomsDataStoreStubController.thirdPartyEmailVerified.url)
        .withBody(createThirdPartyEmailVerifiedBody(eori))
      val result = controller.thirdPartyEmailVerified()(postRequest)
      status(result) shouldBe OK
      contentAsString(result) shouldBe controller.verified
    }

    "return a 200(OK) payload with the expected payload for an EORI number ending in '98'" in {
      val eori = "GB1234567898"
      val postRequest = FakeRequest("POST", routes.CustomsDataStoreStubController.thirdPartyEmailVerified.url)
        .withBody(createThirdPartyEmailVerifiedBody(eori))
      val result = controller.thirdPartyEmailVerified()(postRequest)
      status(result) shouldBe OK
      contentAsString(result) shouldBe controller.undeliverable
    }

    "return a 404(NOT_FOUND) for an EORI number ending in '99'" in {
      val eori = "GB1234567899"
      val postRequest = FakeRequest("POST", routes.CustomsDataStoreStubController.thirdPartyEmailVerified.url)
        .withBody(createThirdPartyEmailVerifiedBody(eori))
      val result = controller.thirdPartyEmailVerified()(postRequest)
      status(result) shouldBe NOT_FOUND
    }
  }
}