/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.customs.declarations.stub.repositories

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsString, Json}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.{AtomicUpdate, ReactiveRepository}
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.{mongoEntity, objectIdFormats}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ClientRepository @Inject()(implicit mc: ReactiveMongoComponent, ec: ExecutionContext)
  extends ReactiveRepository[Client, BSONObjectID]("clients", mc.mongoConnector.db, Client.formats, objectIdFormats)
    with AtomicUpdate[Client] {

  override def indexes: Seq[Index] = Seq(
    Index(Seq("clientId" -> IndexType.Ascending), unique = true, name = Some("clientIdx"))
  )

  def findByClientId(clientId: String): Future[Option[Client]] =
    find("clientId" -> JsString(clientId)).map(_.headOption)

  override def isInsertion(newRecordId: BSONObjectID, oldRecord: Client): Boolean = newRecordId.equals(oldRecord.id)
}

case class Client(clientId: String, callbackUrl: String, token: String, id: BSONObjectID = BSONObjectID.generate())

object Client {

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
