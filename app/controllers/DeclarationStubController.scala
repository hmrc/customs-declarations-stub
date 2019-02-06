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

package controllers

import java.io.StringReader
import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import config.AppConfig
import javax.inject.{Inject, Singleton}
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.transform.{Source => XmlSource}
import javax.xml.validation.{Schema, SchemaFactory}
import play.api.Logger
import play.api.http.{ContentTypes, HeaderNames}
import play.api.libs.json.Json
import play.api.mvc._
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import repositories._
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions, Enrolment}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.mongo.BSONBuilderHelpers
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.wco.dec.MetaData

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.xml.NodeSeq

@Singleton
class DeclarationStubController @Inject()(
  auth: AuthConnector,
  http: HttpClient,
  actorSystem: ActorSystem,
  clientRepo: ClientRepository,
  notificationRepo: NotificationRepository
)(implicit val appConfig: AppConfig, ec: ExecutionContext) extends BaseController with AuthorisedFunctions with BSONBuilderHelpers {

  private val permissibleAcceptHeaders: Set[String] =
    Set("application/vnd.hmrc.1.0+xml", "application/vnd.hmrc.2.0+xml", "application/vnd.hmrc.3.0+xml")

  private val permissibleContentTypes: Set[String] = Set(ContentTypes.XML(Codec.utf_8))

  private val submitSchemas = Seq("/schemas/DocumentMetaData_2_DMS.xsd", "/schemas/WCO_DEC_2_DMS.xsd")

  private val cancelSchemas = Seq("/schemas/CANCEL_METADATA.xsd","/schemas/CANCEL.xsd")

  private val defaultNotification =
    Source.fromInputStream(
      getClass.getResourceAsStream("/messages/example_accepted_import_notification.xml")
    ).getLines().mkString

  override def authConnector: AuthConnector = auth

  def submit(): Action[NodeSeq] = Action.async(parse.xml) { implicit req =>
    validateHeaders() { hdrs =>
      authenticate(hdrs) { client =>
        validatePayload(submitSchemas) { meta =>
          notifyInDueCourse("submit", hdrs, client, meta)
        }
      }
    }
  }

  def cancel(): Action[NodeSeq] = Action.async(parse.xml) { implicit req =>
    validateHeaders() { hdrs =>
      authenticate(hdrs) { client =>
        validatePayload(cancelSchemas) { meta =>
          notifyInDueCourse("cancel", hdrs, client, meta)
        }
      }
    }
  }

  def listClients(): Action[AnyContent] = Action.async { implicit req =>
    clientRepo.findAll().map { found =>
      Ok(Json.toJson(found ++ Seq(appConfig.defaultClient)))
    }
  }

  def addClient(): Action[ClientWrapper] = Action.async(parse.json[ClientWrapper]) { implicit req =>
    val client = req.body.toClient
    clientRepo.atomicUpsert(BSONDocument("clientId" -> client.clientId), setOnInsert(BSONDocument("clientId" -> client.clientId, "callbackUrl" -> client.callbackUrl, "token" -> client.token))).map { res =>
      if (res.writeResult.ok) Created else InternalServerError
    }
  }

  def deleteClient(id: String): Action[AnyContent] = Action.async { implicit req =>
    clientRepo.removeById(BSONObjectID.parse(id).get).map { _ =>
      NoContent
    }
  }

  def listNotifications(): Action[AnyContent] = Action.async { implicit req =>
    notificationRepo.findAll().map { found =>
      Ok(Json.toJson(found))
    }
  }

  def displayNotification(clientId: String, operation: String, lrn: String): Action[AnyContent] =
    Action.async { implicit req =>
      notificationRepo.findByClientAndOperationAndLrn(clientId, operation, lrn).map { found =>
        Ok(Json.toJson(found))
      }
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

  // a dirty approximation of the header validation process implemented by customs declarations API
  private def validateHeaders()(f: ApiHeaders => Future[Result])
                               (implicit req: Request[NodeSeq], hc: HeaderCarrier): Future[Result] = {
    val accept = req.headers.get(HeaderNames.ACCEPT)
    val contentType = req.headers.get(HeaderNames.CONTENT_TYPE)
    val clientId = req.headers.get("X-Client-ID")
    val badgeId = req.headers.get("X-Badge-Identifier")
    if (accept.isEmpty || !permissibleAcceptHeaders.contains(accept.get)) {
      return Future.successful(NotAcceptable)
    }
    if (contentType.isEmpty || !permissibleContentTypes.contains(contentType.get)) {
      return Future.successful(UnsupportedMediaType)
    }
    if (clientId.isEmpty) {
      return Future.successful(InternalServerError)
    }
    f(ApiHeaders(accept.get, contentType.get, clientId.get, badgeId))
  }

  private def authenticate(hdrs: ApiHeaders)(f: Client => Future[Result])
                          (implicit req: Request[NodeSeq], hc: HeaderCarrier): Future[Result] =
    try {
      // we only care about whether the client request can be authorised via the implicit header carrier
      authorised(Enrolment("HMRC-CUS-ORG")) {
        clientRepo.findByClientId(hdrs.clientId).flatMap { maybeClient =>
          val client = maybeClient.getOrElse {
            if (hdrs.clientId == appConfig.defaultClient.clientId) appConfig.defaultClient
            else throw new IllegalArgumentException(s"Unauthorized client: ${hdrs.clientId}")
          }
          f(client)
        }
      }
    } catch {
      case _: Exception => Future.successful(Unauthorized)
    }

  private def validatePayload(schemas: Seq[String])(f: MetaData => Future[Result])
                             (implicit req: Request[NodeSeq], hc: HeaderCarrier): Future[Result] = {
    val schema: Schema = {
      val sources = schemas.map{res => getClass.getResource(res).toString}.map{systemId =>
        new StreamSource(systemId)
      }.toArray[XmlSource]

      SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(sources)
    }
    val xml = req.body.mkString
    val validator = schema.newValidator()

    try {
      validator.validate(new StreamSource(new StringReader(xml)))
    } catch {
      case e: Exception => {
        Logger.warn(s"Invalid XML: ${e.getMessage}\n${xml}", e)
        return Future.successful(BadRequest)
      }
    }

    try {
      f(MetaData.fromXml(xml)) // additionally, catch failure to deserialize as WCO domain case class for the sake of visibility on this kind of error
    } catch {
      case e: Exception => {
        Logger.warn(s"Cannot deserialize XML: ${e.getMessage}", e)
        return Future.successful(BadRequest)
      }
    }
  }

  private def notifyInDueCourse(operation: String, headers: ApiHeaders, client: Client, meta: MetaData)
                               (implicit req: Request[NodeSeq], hc: HeaderCarrier): Future[Result] = {
    val conversationId = UUID.randomUUID().toString
    scheduleEachOnce(operation, headers, conversationId, client,  meta)
    Future.successful(Accepted.withHeaders("X-Conversation-ID" -> conversationId).as(ContentTypes.XML))
  }

  def scheduleEachOnce(operation: String, headers: ApiHeaders, conversationId: String, client: Client, meta: MetaData)
  (implicit req: Request[NodeSeq], hc: HeaderCarrier): Unit =

      actorSystem.scheduler.scheduleOnce(new FiniteDuration(2, TimeUnit.SECONDS)) {
        notificationRepo.findByClientAndOperationAndMetaData(client.clientId, operation, meta).map { maybeNotification =>
        Logger.info("Entering async request notification")
        val xml = maybeNotification.map(_.xml).getOrElse(defaultNotification)
        Logger.debug(s"scheduling one notification call ${xml.toString()}")
        sendNotificationWithDelay(client, conversationId, xml).onComplete { _ =>
          Logger.info("Exiting async request notification")
        }
      }
  }

  private def sendNotificationWithDelay(client: Client, conversationId: String, xml: String, delay: Duration = 5.seconds)
                                       (implicit rds: HttpReads[HttpResponse], ec: ExecutionContext): Future[HttpResponse] = {
    http.POSTString(
      client.callbackUrl,
      xml,
      Seq(
        HeaderNames.AUTHORIZATION -> s"Bearer ${client.token}",
        HeaderNames.CONTENT_TYPE -> ContentTypes.XML,
        "X-Conversation-ID" -> conversationId
      )
    )(rds, HeaderCarrier(), ec)
  }
}

case class ApiHeaders(accept: String, contentType: String, clientId: String, badgeId: Option[String])
