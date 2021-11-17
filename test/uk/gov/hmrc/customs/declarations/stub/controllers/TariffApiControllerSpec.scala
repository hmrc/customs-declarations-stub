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

import akka.actor.ActorSystem
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

class TariffApiControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  override lazy val app: Application = GuiceApplicationBuilder().build

  implicit val actorSystem = ActorSystem()

  "TariffApiController.commodities" should {

    "return OK with a Json body of the expected size" when {

      "the given commodity code is equal to '2208303000'" in {
        val result = route(app, FakeRequest("GET", "/api/v2/commodities/2208303000")).get

        status(result) shouldBe OK
        contentAsString(result).size shouldBe 241972
      }

      "the given commodity code is equal to '3903110000'" in {
        val result = route(app, FakeRequest("GET", "/api/v2/commodities/3903110000")).get

        status(result) shouldBe OK
        contentAsString(result).size shouldBe 323930
      }

      "the given commodity code is equal to '6103230000'" in {
        val result = route(app, FakeRequest("GET", "/api/v2/commodities/6103230000")).get

        status(result) shouldBe OK
        contentAsString(result).size shouldBe 350063
      }

      "the given commodity code is equal to '7114110000'" in {
        val result = route(app, FakeRequest("GET", "/api/v2/commodities/7114110000")).get

        status(result) shouldBe OK
        contentAsString(result).size shouldBe 351890
      }

      "the last digit of the given commodity code is '0'" in {
        val result = route(app, FakeRequest("GET", "/api/v2/commodities/1234567890")).get

        status(result) shouldBe OK
        contentAsString(result).size shouldBe 261495
      }

      "the last digit of the given commodity code is '1'" in {
        val result = route(app, FakeRequest("GET", "/api/v2/commodities/1234567891")).get

        status(result) shouldBe OK
        contentAsString(result).size shouldBe 261495
      }

      "the last digit of the given commodity code is NOT '0' and NOT '1'" in {
        val result = route(app, FakeRequest("GET", "/api/v2/commodities/1234567892")).get

        status(result) shouldBe OK
        contentAsString(result).size shouldBe 326756
      }
    }

    "return NOT_FOUND" when {
      "the last digit of the given commodity code is '9'" in {
        val result = route(app, FakeRequest("GET", "/api/v2/commodities/0123456789")).get

        status(result) shouldBe NOT_FOUND
      }
    }
  }
}
