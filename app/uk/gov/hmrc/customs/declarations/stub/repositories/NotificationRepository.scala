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

package uk.gov.hmrc.customs.declarations.stub.repositories

import com.mongodb.client.model.Indexes.ascending
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.{IndexModel, IndexOptions}
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.wco.dec.MetaData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NotificationRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[Notification](
      mongoComponent = mongoComponent,
      collectionName = "notifications",
      domainFormat = Notification.format,
      indexes = NotificationRepository.indexes
    ) {

  def findByClientAndOperationAndMetaData(clientId: String, operation: String, meta: MetaData): Future[Option[Notification]] = {
    val lrn = meta.declaration.flatMap(_.functionalReferenceId).getOrElse("")
    val filter = Json.obj("clientId" -> clientId, "operation" -> operation, "lrn" -> lrn)
    collection.find(BsonDocument(filter.toString)).limit(1).toFuture.map(_.headOption)
  }
}

object NotificationRepository {
  // clientId, operation and lrn constitute a natural key for notification; i.e. 1 notification per operation per client
  val indexes: Seq[IndexModel] = List(IndexModel(ascending("clientId", "operation", "lrn"), IndexOptions().name("notificationIdx").unique(true)))
}

case class Notification(clientId: String, operation: String, lrn: String, xml: String)

object Notification {
  implicit val format: Format[Notification] = Json.format[Notification]
}
