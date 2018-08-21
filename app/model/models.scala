/*
 * Copyright 2018 HM Revenue & Customs
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

package model

import uk.gov.hmrc.customs.api.common.controllers.ErrorResponse


sealed trait ApiVersion {
  val value: String
  val configPrefix: String
  override def toString: String = value
}
object VersionOne extends ApiVersion{
  override val value: String = "1.0"
  override val configPrefix: String = ""
}
object VersionTwo extends ApiVersion{
  override val value: String = "2.0"
  override val configPrefix: String = "v2."
}
object VersionThree extends ApiVersion{
  override val value: String = "3.0"
  override val configPrefix: String = "v3."
}

object ErrorResponse {
  import play.api.libs.json.{JsValue, Json, Writes}

  implicit val writes = new Writes[ErrorResponse] {
    def writes(e: ErrorResponse): JsValue = Json.obj("code" -> e.errorCode, "message" -> e.message)
  }
}
