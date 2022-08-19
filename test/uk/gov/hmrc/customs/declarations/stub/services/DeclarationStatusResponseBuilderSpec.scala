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

package uk.gov.hmrc.customs.declarations.stub.services

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.customs.declarations.stub.models.declarationstatus.DeclarationStatusResponse
import uk.gov.hmrc.customs.declarations.stub.models.declarationstatus.DeclarationStatusResponse.{NotFoundResponse, SuccessfulResponse}
import uk.gov.hmrc.customs.declarations.stub.testdata.CommonTestData

class DeclarationStatusResponseBuilderSpec extends AnyWordSpec with Matchers {

  private val declarationStatusResponseBuilder = new DeclarationStatusResponseBuilder()

  "DeclarationStatusResponseBuilder on buildDeclarationStatus" when {

    val eori = CommonTestData.eori

    "provided with MRN ending with '9999'" should {

      "return NotFoundResponse with correct body" in {

        val mrn = "18GB9JLC3CU1LF9999"

        val result = declarationStatusResponseBuilder.buildDeclarationStatus(eori, mrn)

        val expectedResult = NotFoundResponse

        result mustBe expectedResult
      }
    }

    "provided with MRN not ending with '9999'" should {

      "return SuccessfulResponse with correct body" in {

        val mrn = CommonTestData.mrn

        val result = declarationStatusResponseBuilder.buildDeclarationStatus(eori, mrn)

        val expectedResult = SuccessfulResponse(DeclarationStatusResponse.successfulResponseBody(eori, mrn))

        result mustBe expectedResult
      }
    }
  }

}
