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
import play.api.mvc.Codec
import uk.gov.hmrc.customs.test.CustomsPlaySpec

class SubmissionControllerSpec extends CustomsPlaySpec {

  val method = "POST"
  val uri = uriWithContextPath("/")
  val cancelUri = uriWithContextPath("/cancellation-requests")
  val validXML = <MetaData xmlns="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2"><wstxns1:Declaration xmlns:wstxns1="urn:wco:datamodel:WCO:DEC-DMS:2"/></MetaData>

  val headers: Map[String, String] = Map(
    "X-Client-ID" -> "1234",
    HeaderNames.ACCEPT -> s"application/vnd.hmrc.${2.0}+xml",
    HeaderNames.CONTENT_TYPE -> ContentTypes.XML(Codec.utf_8),
   "X-Badge-Identifier" -> "badgeIdentifier1")

  s"$method $uri" should {

    "return 202" in  {
      requestScenario(method, uri, headers = headers, body=validXML.toString) { wasOk }
    }

    "return 400 for an invalid XML" in {
      requestScenario(method, uri,headers = headers, body="<declaration></nodeclaration>") { res=> wasBadRequest(res) }
    }
    "should validate headers" in {
      requestScenario(method, uri,headers = headers,body="<declaration></nodeclaration>") { res=> wasBadRequest(res) }
    }
  }
  s"$method $cancelUri" should {

    "return 202" in  {
      requestScenario(method, uri, headers = headers, body=validXML.toString) { wasOk }
    }

    "return 400 for an invalid XML" in {
      requestScenario(method, uri,headers = headers, body="<declaration></nodeclaration>") { res=> wasBadRequest(res) }
    }
  }

}
