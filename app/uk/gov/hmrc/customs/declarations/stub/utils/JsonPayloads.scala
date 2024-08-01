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

package uk.gov.hmrc.customs.declarations.stub.utils

import java.nio.file.{Files, Path}

import org.apache.pekko.stream.scaladsl.FileIO
import play.api.http.HttpEntity
import play.api.http.Status.OK
import play.api.mvc.{ResponseHeader, Result}

object JsonPayloads {

  def fromPath(path: Path): Result = {
    val source = FileIO.fromPath(path)
    val contentLength = Some(Files.size(path))

    Result(header = ResponseHeader(OK, Map.empty), body = HttpEntity.Streamed(source, contentLength, Some("application/json")))
  }
}
