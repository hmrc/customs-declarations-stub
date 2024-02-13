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

package uk.gov.hmrc.customs.declarations.stub.models.upscan

import play.api.libs.json.{Format, JsError, JsObject, JsResult, JsString, JsSuccess, JsValue, Json, OFormat}

sealed trait FileState
final case class Waiting(uploadRequest: UploadRequest) extends FileState
case object Uploaded extends FileState
case object Successful extends FileState
case object Failed extends FileState
case object VirusDetected extends FileState
case object UnacceptableMimeType extends FileState

object Waiting {

  implicit val format: OFormat[Waiting] = Json.format[Waiting]
}

object FileState {

  private val waiting = "waiting"
  private val uploaded = "uploaded"
  private val success = "success"
  private val failed = "failed"
  private val virus = "virus"
  private val mimeType = "mimeType`"

  implicit val format: Format[FileState] = new Format[FileState] {
    override def writes(o: FileState): JsValue = o match {
      case Waiting(request)     => Json.obj(waiting -> Json.toJson(request))
      case Uploaded             => JsString(uploaded)
      case Successful           => JsString(success)
      case Failed               => JsString(failed)
      case VirusDetected        => JsString(virus)
      case UnacceptableMimeType => JsString(mimeType)
    }

    override def reads(json: JsValue): JsResult[FileState] = json match {
      case JsString(`uploaded`) => JsSuccess(Uploaded)
      case JsString(`success`)  => JsSuccess(Successful)
      case JsString(`failed`)   => JsSuccess(Failed)
      case JsString(`virus`)    => JsSuccess(VirusDetected)
      case JsString(`mimeType`) => JsSuccess(UnacceptableMimeType)
      case JsObject(map) =>
        map.get(waiting) match {
          case Some(request) => Json.fromJson[UploadRequest](request).map(Waiting(_))
          case None          => JsError("Unable to parse FileState")
        }
      case _ => JsError("Unable to parse FileState")
    }
  }
}
