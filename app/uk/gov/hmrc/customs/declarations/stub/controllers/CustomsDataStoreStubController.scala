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

import scala.concurrent.Future

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton
class CustomsDataStoreStubController @Inject()(cc: ControllerComponents) extends BackendController(cc) {

  lazy val verified = """{"address":"some@email.com","timestamp":"1987-03-20T01:02:03Z"}"""

  def emailIfVerified(eori: String): Action[AnyContent] = Action.async { _ =>
    Future.successful {
      eori match {
        case _ if eori.length < 2           => BadRequest("Invalid EORI Number")
        case _ if eori.takeRight(2) == "99" => NotFound("The email address is not verified")
        case _                              => Ok(verified)
      }
    }
  }
}
