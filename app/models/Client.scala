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

package models

import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.mongoEntity

case class Client(clientId: String, callbackUrl: String, token: String, id: BSONObjectID = BSONObjectID.generate())

object Client {
  implicit val bsonFormat = Json.format[BSONObjectID]
  implicit val formats = mongoEntity {
    Json.format[Client]
  }
}

case class ClientWrapper(clientId: String, callbackUrl: String, token: String) {
  def toClient: Client = Client(clientId, callbackUrl, token)
}

object ClientWrapper {
  implicit val formats = Json.format[ClientWrapper]
}