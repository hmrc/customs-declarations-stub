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

import java.util.UUID

import config.AppConfig
import javax.inject.{Inject, Singleton}
import model._
import play.api.Logger
import play.api.http.MimeTypes
import play.api.http.HeaderNames._
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.customs.api.common.controllers.ErrorResponse._

import scala.concurrent.Future
import scala.xml.NodeSeq


@Singleton
class SubmissionsController @Inject()(implicit val appConfig: AppConfig) extends BaseController with HeaderValidator {

  def submit: Action[AnyContent] = validateHeaders.async { implicit request =>
      processRequest()
  }

  def cancel() :Action[AnyContent] = validateHeaders.async { implicit request =>
    processRequest()
  }

  def validateXml(data:Option[NodeSeq]) = {
    data  match {
      case Some(xml) => true
      case None => false
    }
  }
  def processRequest()(implicit request: Request[AnyContent]) :  Future[Result] =  {
    Logger.debug(s"Request received. Payload = ${request.body.asXml}")
    if(validateXml(request.body.asXml))
      Future.successful(Accepted.as(MimeTypes.XML).withHeaders( "X-Conversation-ID" -> UUID.randomUUID.toString))
    else
      Future.successful(BadRequest("Invalid XML"))
  }
}

trait HeaderValidator extends Results{

  val XClientIdHeaderName = "X-Client-ID"
  val XBadgeIdentifierHeaderName: String = "X-Badge-Identifier"

  private val versionsByAcceptHeader: Map[String, ApiVersion] = Map(
    "application/vnd.hmrc.1.0+xml" -> VersionOne,
    "application/vnd.hmrc.2.0+xml" -> VersionTwo,
    "application/vnd.hmrc.3.0+xml" -> VersionThree
  )

  private lazy val validContentTypeHeaders = Seq(MimeTypes.XML, MimeTypes.XML + ";charset=utf-8", MimeTypes.XML + "; charset=utf-8")
  private lazy val xClientIdRegex = "^\\S+$".r

  def validateHeaders() = new ActionBuilder[Request] {

    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {

      val hasAccept = request.headers.get(ACCEPT).map{versionsByAcceptHeader.keySet.contains(_)}.getOrElse(false)

      val hasContentType = request.headers.get(CONTENT_TYPE).map{res=> validContentTypeHeaders.contains(res.toLowerCase())}.getOrElse(false)
      val hasClientID = request.headers.get(XClientIdHeaderName).map{res=> xClientIdRegex.findFirstIn(res).nonEmpty}.getOrElse(false)

      Logger.debug(s"hasAccept && hasContentType && hasClientID $hasAccept && $hasContentType && $hasClientID " +
        s"and result is ${hasAccept && hasContentType && hasClientID}")

      if(!hasAccept) Future.successful(ErrorAcceptHeaderInvalid.XmlResult)
          else if(!hasContentType) Future.successful(ErrorContentTypeHeaderInvalid.XmlResult)
          else if(!hasClientID) Future.successful(ErrorInternalServerError.XmlResult)
        else block(request)

      }
    }

}
