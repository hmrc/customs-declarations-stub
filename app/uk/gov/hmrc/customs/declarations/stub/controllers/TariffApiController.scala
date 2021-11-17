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
    val commodityCode = code.filter(_.isDigit)
    if (commodityCode.length != 10 || commodityCode.takeRight(1) == "9") NotFound("Commodity was not found.")
    else {
      val uri = getClass.getResource(s"/messages/${filename(commodityCode)}").toURI
      JsonPayloads.fromPath(Paths.get(uri))
    }
  }

  private def filename(commodityCode: String): String = {
    val digit = commodityCode.takeRight(1)
    commodityCode match {
      case "2208303000"                      => "supplementary-units-2208303000.json"
      case "3903110000"                      => "supplementary-units-3903110000.json"
      case _ if digit == "0" || digit == "1" => "supplementary-units-required.json"
      case _                                 => "supplementary-units-not-required.json"
    }
  }
}
