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

package controllers

import config.AppConfig
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.http.MimeTypes
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.Future
import scala.util.{Try, Random}

@Singleton
class SubmissionsController @Inject()(implicit val appConfig: AppConfig) extends BaseController {

  def post: Action[AnyContent] = Action.async { implicit request =>

    Logger.debug(s"Request received. Payload = ${request.body.toString} headers = ${request.headers.headers}")
    if(validateXml(request.body.asText.getOrElse("")).isSuccess) Future.successful(Accepted.as(MimeTypes.XML).withHeaders( "X-Conversation-ID" -> Random.nextLong.toString))
    else
      Future.successful(BadRequest("Invalid XML"))
  }
  def validateXml(data:String) =  Try(scala.xml.XML.loadString(data))

}
