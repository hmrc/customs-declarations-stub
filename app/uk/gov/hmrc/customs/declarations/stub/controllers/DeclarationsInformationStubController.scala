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

import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.customs.declarations.stub.controllers.actions.AuthAction
import uk.gov.hmrc.customs.declarations.stub.models.declarationstatus.DeclarationStatusResponse._
import uk.gov.hmrc.customs.declarations.stub.services.DeclarationStatusResponseBuilder
import uk.gov.hmrc.customs.declarations.stub.utils.XmlPayloads
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}

@Singleton
class DeclarationsInformationStubController @Inject() (
  cc: ControllerComponents,
  authenticate: AuthAction,
  declarationStatusResponseBuilder: DeclarationStatusResponseBuilder
) extends BackendController(cc) {

  def getDeclaration(mrn: String, declarationVersion: Option[Int] = None): Action[AnyContent] = authenticate { request =>
    if (request.eori.endsWith("8888")) NotFound
    else {
      declarationVersion match {
        case Some(1 | 2) | None =>
          Ok(XmlPayloads.declaration(declarationVersion getOrElse 2, mrn))

        case Some(_) => NotFound
      }
    }
  }
  def getDeclarationStatus(mrn: String): Action[AnyContent] = authenticate { request =>
    val response = declarationStatusResponseBuilder.buildDeclarationStatus(request.eori, mrn)

    response match {
      case NotFoundResponse => NotFound(response.body)
      case _                => Ok(response.body)
    }
  }
}
