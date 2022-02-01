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

import play.api.http.ContentTypes
import play.api.mvc.{Action, MessagesControllerComponents}
import play.api.Logging
import uk.gov.hmrc.customs.declarations.stub.config.AppConfig
import uk.gov.hmrc.customs.declarations.stub.models.upscan._
import uk.gov.hmrc.customs.declarations.stub.models.upscan.Field._
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.xml._

@Singleton
class UpscanStubController @Inject()(appConfig: AppConfig, httpClient: HttpClient, mcc: MessagesControllerComponents)
    extends BackendController(mcc) with Logging {

  implicit val ec = mcc.executionContext

  def waiting(ref: String) =
    Waiting(
      UploadRequest(
        href = s"${appConfig.cdsFileUploadFrontendBaseUrl}/cds-file-upload-service/test-only/s3-bucket",
        fields = Map(
          Algorithm.toString -> "AWS4-HMAC-SHA256",
          Signature.toString -> "xxxx",
          Key.toString -> "xxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
          ACL.toString -> "private",
          Credentials.toString -> "ASIAxxxxxxxxx/20180202/eu-west-2/s3/aws4_request",
          Policy.toString -> "xxxxxxxx==",
          SuccessRedirect.toString -> s"${appConfig.cdsFileUploadFrontendBaseUrl}/cds-file-upload-service/upload/upscan-success/${ref}",
          ErrorRedirect.toString -> s"${appConfig.cdsFileUploadFrontendBaseUrl}/cds-file-upload-service/upload/upscan-error/${ref}"
        )
      )
    )

  // for now, we will just return some random
  def handleBatchFileUploadRequest: Action[NodeSeq] = Action(parse.xml) { implicit req =>
    val xmlBodyString = req.body.mkString
    logger.info(s"Batch file upload request: $xmlBodyString")
    Thread.sleep(100)

    val fileGroupSize = (scala.xml.XML.loadString(xmlBodyString) \ "FileGroupSize").text.toInt

    val resp = FileUploadResponse((1 to fileGroupSize).map { i =>
      FileUpload(i.toString, waiting(i.toString), id = s"$i")
    }.toList)

    val xmlResp = XmlHelper.toXml(resp)

    logger.debug(s"Batch file upload response: $xmlResp")

    Ok(xmlResp).as(ContentTypes.XML)
  }
}

object XmlHelper {

  def toXml(field: (String, String)): Elem =
    <a/>.copy(label = field._1, child = Seq(Text(field._2)))

  def toXml(uploadRequest: UploadRequest): Elem =
    <UploadRequest>
      <Href>
        {uploadRequest.href}
      </Href>
      <Fields>
        {uploadRequest.fields.map(toXml)}
      </Fields>
    </UploadRequest>

  def toXml(upload: FileUpload): Elem = {
    val request = upload.state match {
      case Waiting(req) => toXml(req)
      case _            => NodeSeq.Empty
    }
    <File>
      <Reference>
        {upload.reference}
      </Reference>{request}
    </File>
  }

  def toXml(response: FileUploadResponse): Elem =
    <FileUploadResponse xmlns="hmrc:fileupload">
      <Files>
        {response.uploads.map(toXml)}
      </Files>
    </FileUploadResponse>
}
