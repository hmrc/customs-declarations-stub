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

package uk.gov.hmrc.customs.declarations.stub.controllers

import org.scalatest.{Matchers, WordSpec}
import play.api.test.FakeRequest
import play.api.test.Helpers._

class CustomsDataStoreStubControllerSpec extends WordSpec with Matchers {

  private val controller = new CustomsDataStoreStubController(stubControllerComponents())
  private val request = FakeRequest()

  "GET emailIfVerified" should {

    "return a 200(OK) payload with the expected payload for an EORI number not ending in '99'" in {
      val eori = "GB1234567890"
      val result = controller.emailIfVerified(eori)(request)
      status(result) shouldBe OK
      contentAsString(result) shouldBe controller.verified
    }

    "return a 400(BAD_REQUEST) for an invalid EORI number" in {
      val eori = "G"
      val result = controller.emailIfVerified(eori)(request)
      status(result) shouldBe BAD_REQUEST
    }

    "return a 404(NOT_FOUND) for an EORI number ending in '99'" in {
      val eori = "GB1234567899"
      val result = controller.emailIfVerified(eori)(request)
      status(result) shouldBe NOT_FOUND
    }
  }
}
