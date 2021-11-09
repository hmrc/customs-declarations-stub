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

import java.nio.file.Paths

import javax.inject.{Inject, Singleton}
import play.api.mvc._
import uk.gov.hmrc.customs.declarations.stub.utils.JsonPayloads
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton
class TariffApiController @Inject()(cc: ControllerComponents) extends BackendController(cc) {

  def commodities(code: String): Action[AnyContent] = Action { _ =>
    val lastDigit = code.filter(_.isDigit).takeRight(1)
    val filename =
      if (lastDigit == "1") "supplementary-units-required.json" else "supplementary-units-not-required.json"
    JsonPayloads.fromPath(Paths.get(getClass.getResource(s"/messages/$filename").toURI))
  }
}
