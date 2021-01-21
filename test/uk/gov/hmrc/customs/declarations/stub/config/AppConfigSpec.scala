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

package uk.gov.hmrc.customs.declarations.stub.config

import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

class AppConfigSpec extends WordSpec with MustMatchers with GuiceOneAppPerSuite {

  override lazy val app: Application = GuiceApplicationBuilder().build()

  val config: AppConfig = app.injector.instanceOf[AppConfig]

  "Application configuration" should {
    "contains correct client information" in {
      val client = config.defaultClient

      client.clientId must be("customs-declare-exports")
      client.callbackUrl must be("http://localhost:6792/customs-declare-exports/notify")
      client.token must be("abc59609za2q")
    }
  }
}
