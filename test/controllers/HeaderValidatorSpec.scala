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

package controllers

import play.api.http.{ContentTypes, HeaderNames}
import play.api.libs.json.Json
import play.api.mvc.{Request, AnyContent, Result, Codec}
import play.api.test.FakeRequest
import uk.gov.hmrc.customs.test.CustomsPlaySpec


class HeaderValidatorSpec extends CustomsPlaySpec with HeaderValidator{

  val request =  FakeRequest("POST", "/").withHeaders(Map.empty.toSeq: _*)

  val validHeaders: Map[String, String] = Map(
    "X-Client-ID" -> "1234",
    HeaderNames.ACCEPT -> s"application/vnd.hmrc.${2.0}+xml",
    HeaderNames.CONTENT_TYPE -> ContentTypes.XML(Codec.utf_8),
    "X-Badge-Identifier" -> "badgeIdentifier1")

  val block: (Request[AnyContent] => Result) = request => (Status(202)(Json.toJson("Ok")))

  "HeaderValidator" should {
    "validate request for ACCEPT header" in {
      wasNotAcceptable(validateHeaders()(block).apply(request))
    }
    "validate request for CONTENT_TYPE header" in {
      wasUnsupported(validateHeaders()(block).apply(request.withHeaders(HeaderNames.ACCEPT -> s"application/vnd.hmrc.${2.0}+xml")))
    }
    "process request if headers are available" in {
      wasOk(validateHeaders()(block).apply(request.withHeaders(validHeaders.toSeq: _*)))
    }

  }

}
