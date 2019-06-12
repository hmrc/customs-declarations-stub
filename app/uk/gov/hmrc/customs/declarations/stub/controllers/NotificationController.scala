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

package uk.gov.hmrc.customs.declarations.stub.controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.customs.declarations.stub.config.AppConfig
import uk.gov.hmrc.customs.declarations.stub.repositories.{ClientRepository, Notification, NotificationRepository}
import uk.gov.hmrc.mongo.BSONBuilderHelpers
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.ExecutionContext
import scala.xml.NodeSeq

@Singleton
class NotificationController @Inject()(
  auth: AuthConnector,
  http: HttpClient,
  clientRepo: ClientRepository,
  notificationRepo: NotificationRepository
)(implicit val appConfig: AppConfig, ec: ExecutionContext)
    extends BaseController with AuthorisedFunctions with BSONBuilderHelpers {

  override def authConnector: AuthConnector = auth

  def listNotifications(): Action[AnyContent] = Action.async { implicit req =>
    notificationRepo.findAll().map(found => Ok(Json.toJson(found)))
  }

  def displayNotification(clientId: String, operation: String, lrn: String): Action[AnyContent] =
    Action.async { implicit req =>
      notificationRepo.findByClientAndOperationAndLrn(clientId, operation, lrn).map(found => Ok(Json.toJson(found)))
    }

  def addNotification(clientId: String, operation: String, lrn: String): Action[NodeSeq] =
    Action.async(parse.xml) { implicit req =>
      notificationRepo.insert(Notification(clientId, operation, lrn, req.body.mkString)).map { result =>
        if (result.ok) Created else InternalServerError
      }
    }

  def deleteNotification(id: String): Action[NodeSeq] = Action.async(parse.xml) { implicit req =>
    notificationRepo.removeById(BSONObjectID.parse(id).get).map { result =>
      if (result.ok) NoContent else InternalServerError
    }
  }
}
