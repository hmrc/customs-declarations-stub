/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.libs.json.{Format, JsString, Json}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.{mongoEntity, objectIdFormats}
import uk.gov.hmrc.wco.dec.MetaData

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NotificationRepository @Inject()(implicit mc: ReactiveMongoComponent, ec: ExecutionContext)
    extends ReactiveRepository[Notification, BSONObjectID](
      "notifications",
      mc.mongoConnector.db,
      Notification.formats,
      objectIdFormats
    ) {

  // clientId, lrn, and operation constitute a natural key for notification; i.e. 1 notification per operation per client
  override def indexes: Seq[Index] = Seq(
    Index(
      Seq("clientId" -> IndexType.Ascending, "operation" -> IndexType.Ascending, "lrn" -> IndexType.Ascending),
      unique = true,
      name = Some("notificationIdx")
    )
  )

  def findByClientAndOperationAndMetaData(
    clientId: String,
    operation: String,
    meta: MetaData
  ): Future[Option[Notification]] =
    findByClientAndOperationAndLrn(clientId, operation, meta.declaration.flatMap(_.functionalReferenceId).getOrElse(""))

  def findByClientAndOperationAndLrn(clientId: String, operation: String, lrn: String): Future[Option[Notification]] =
    find("clientId" -> JsString(clientId), "operation" -> JsString(operation), "lrn" -> JsString(lrn)).map(_.headOption)

}

case class Notification(
  clientId: String,
  operation: String,
  lrn: String,
  xml: String,
  id: BSONObjectID = BSONObjectID.generate()
)

object Notification {

  implicit val formats: Format[Notification] = mongoEntity {
    Json.format[Notification]
  }

}
