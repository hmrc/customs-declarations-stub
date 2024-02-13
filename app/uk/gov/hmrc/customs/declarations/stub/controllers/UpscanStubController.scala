/*
 * Copyright 2023 HM Revenue & Customs
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
import scala.concurrent.ExecutionContext
import scala.xml._

@Singleton
class UpscanStubController @Inject() (appConfig: AppConfig, httpClient: HttpClient, mcc: MessagesControllerComponents)
    extends BackendController(mcc) with Logging {

  implicit val ec: ExecutionContext = mcc.executionContext

  def waiting(sequenceNo: String) = {
    /*
      The first file in a batch upload is always the contacts file created and uploaded by the SFUS frontend itself.

      In non-E2E environments (like development, staging and ET) the SFUS frontend exposed a test-only endpoint to mock
      the S3 bucket url that files are normally upload to. So in effect for this first file the SFUS frontend is calling itself!

      The domain names that a user can access from their browser and the the service can access from its MDTP environment
      are not the same. So to facilitate the testing of the SFUS frontend in non-E2E environments the stub has this
      special behaviour of switching the domain name for uploading the first contacts file to an internal domain that the
      SFUS frontend can reach.
     */
    val sfusFrontendBaseUrl =
      if (sequenceNo == "1")
        appConfig.cdsFileUploadFrontendInternalBaseUrl
      else
        appConfig.cdsFileUploadFrontendPublicBaseUrl

    Waiting(
      UploadRequest(
        href = s"${sfusFrontendBaseUrl}/cds-file-upload-service/test-only/s3-bucket",
        fields = Map(
          Algorithm.toString -> "AWS4-HMAC-SHA256",
          Signature.toString -> "xxxx",
          Key.toString -> "xxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
          ACL.toString -> "private",
          Credentials.toString -> "ASIAxxxxxxxxx/20180202/eu-west-2/s3/aws4_request",
          Policy.toString -> "xxxxxxxx==",
          SuccessRedirect.toString -> s"${appConfig.cdsFileUploadFrontendPublicBaseUrl}/cds-file-upload-service/upload/upscan-success/${sequenceNo}",
          ErrorRedirect.toString -> s"${appConfig.cdsFileUploadFrontendPublicBaseUrl}/cds-file-upload-service/upload/upscan-error/${sequenceNo}"
        )
      )
    )
  }

  // for now, we will just return some random
  def handleBatchFileUploadRequest: Action[NodeSeq] = Action(parse.xml) { implicit req =>
    val xmlBodyString = req.body.mkString
    logger.info(s"Batch file upload request: $xmlBodyString")
    Thread.sleep(100)

    val files = (scala.xml.XML.loadString(xmlBodyString) \\ "File").toSeq

    val fileUploads = files.map { node =>
      val fileSequenceNo = node \ "FileSequenceNo"

      FileUpload(fileSequenceNo.text, waiting(fileSequenceNo.text), id = fileSequenceNo.text)
    }.toList

    val resp = FileUploadResponse(fileUploads.toList)
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
