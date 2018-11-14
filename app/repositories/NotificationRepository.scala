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

package repositories

import javax.inject.{Inject, Singleton}
import models.Notification
import play.api.libs.json.JsString
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.objectIdFormats
import uk.gov.hmrc.wco.dec.{Declaration, MetaData}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NotificationRepository @Inject()(implicit mc: ReactiveMongoComponent, ec: ExecutionContext)
  extends ReactiveRepository[Notification, BSONObjectID]("clients", mc.mongoConnector.db, Notification.formats, objectIdFormats) {

  // clientId, lrn, and operation constitute a natural key for notification; i.e. 1 notification per operation per client
  override def indexes: Seq[Index] = Seq(
    Index(
      Seq(
        "clientId" -> IndexType.Ascending,
        "operation" -> IndexType.Ascending,
        "lrn" -> IndexType.Ascending
      ),
      unique = true,
      name = Some("notificationIdx")
    )
  )

  def findByClientAndOperationAndMetaData(clientId: String, operation: String, meta: MetaData): Future[Option[Notification]] = {
    val declaration = meta.declaration.getOrElse(Declaration())

    findByClientAndOperationAndLrn(clientId, operation, declaration.functionalReferenceId.getOrElse(""))
  }

  def findByClientAndOperationAndLrn(clientId: String, operation: String, lrn: String): Future[Option[Notification]] =
    find("clientId" -> JsString(clientId), "operation" -> JsString(operation), "lrn" -> JsString(lrn)).map(_.headOption)

}
