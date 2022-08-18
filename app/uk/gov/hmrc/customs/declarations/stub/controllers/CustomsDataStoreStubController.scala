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

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton
class CustomsDataStoreStubController @Inject() (cc: ControllerComponents) extends BackendController(cc) {

  lazy val verified = """{"address":"some@email.com","timestamp":"1987-03-20T01:02:03Z"}"""
  lazy val undeliverable: String = """{
                             |    "address": "some@email.com",
                             |    "timestamp": "2020-03-20T01:02:03Z",
                             |    "undeliverable": {
                             |          "subject": "subject-example",
                             |          "eventId": "example-id",
                             |          "groupId": "example-group-id",
                             |          "timestamp": "2021-05-14T10:59:45.811+01:00",
                             |          "event": {
                             |                     "id": "example-id",
                             |                    "event": "someEvent",
                             |                    "emailAddress": "some@email.com",
                             |                    "detected": "2021-05-14T10:59:45.811+01:00",
                             |                    "code": 12,
                             |                    "reason": "Inbox full",
                             |                    "enrolment": "HMRC-CUS-ORG~EORINumber~testEori"
                             |        }
                             |     }
                             |}""".stripMargin

  def emailIfVerified(eori: String): Action[AnyContent] = Action {
    eori.takeRight(2) match {
      case "99" => NotFound("The email address is not verified")
      case "98" => Ok(undeliverable)
      case _    => Ok(verified)
    }
  }
}
