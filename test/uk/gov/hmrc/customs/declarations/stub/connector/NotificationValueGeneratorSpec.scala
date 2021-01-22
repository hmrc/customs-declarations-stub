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

import uk.gov.hmrc.customs.declarations.stub.generators.NotificationValueGenerator
import uk.gov.hmrc.play.test.UnitSpec

class NotificationValueGeneratorSpec extends UnitSpec {

  val testObj = new NotificationValueGenerator

  "NotificationValueGenerator" should {
    "generate an MRN of the correct length" in {
      val result = testObj.generateMRN()
      result.length should be(18)
    }
  }

}
