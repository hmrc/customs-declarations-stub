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

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton
class CustomsDataStoreStubController @Inject() (cc: ControllerComponents) extends BackendController(cc) {

  lazy val verified = """{"address":"some@email.com","timestamp":"1987-03-20T01:02:03Z"}"""
  lazy val undeliverable: String = """
      |{
      |  "address": "some@email.com",
      |  "timestamp": "2024-06-26T08:06:19Z",
      |  "undeliverable": {
      |    "subject": "subject-example",
      |    "eventId": "example-id",
      |    "groupId": "example-group-id",
      |    "timestamp": "2024-06-26T13:39:08.990219239",
      |    "event": {
      |      "id": "example-id",
      |      "event": "PermanentBounce",
      |      "emailAddress": "some@email.com",
      |      "detected": "2024-06-26T14:37:26.906Z",
      |      "code": 9002,
      |      "reason": "Recipient has not consented to message",
      |      "enrolment": "HMRC-CUS-ORG~EORINumber~testEori"
      |    }
      |  }
      |}""".stripMargin

  def emailIfVerified(eori: String): Action[AnyContent] = Action {
    eori.takeRight(2) match {
      case "99" => NotFound("The email address is not verified")
      case "98" => Ok(undeliverable)
      case _    => Ok(verified)
    }
  }
}
