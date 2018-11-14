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
import models.Client
import play.api.libs.json.JsString
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.objectIdFormats

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ClientRepository @Inject()(implicit mc: ReactiveMongoComponent, ec: ExecutionContext)
  extends ReactiveRepository[Client, BSONObjectID]("clients", mc.mongoConnector.db, Client.formats, objectIdFormats) {

  override def indexes: Seq[Index] = Seq(
    Index(Seq("clientId" -> IndexType.Ascending), unique = true, name = Some("clientIdx"))
  )

  def findByClientId(clientId: String): Future[Option[Client]] =
    find("clientId" -> JsString(clientId)).map(_.headOption)
}
