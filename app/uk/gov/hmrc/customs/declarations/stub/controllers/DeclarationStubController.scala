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

package uk.gov.hmrc.customs.declarations.stub.controllers

import java.io.{IOException, StringReader}
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.xml.NodeSeq

import javax.inject.{Inject, Singleton}
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.transform.{Source => XmlSource}
import javax.xml.validation.{Schema, SchemaFactory}
import org.xml.sax.SAXException
import play.api.Logging
import play.api.http.{ContentTypes, HeaderNames, MimeTypes}
import play.api.libs.json.Json
import play.api.mvc._
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions, Enrolment}
import uk.gov.hmrc.customs.declarations.stub.config.AppConfig
import uk.gov.hmrc.customs.declarations.stub.connector.NotificationConnector
import uk.gov.hmrc.customs.declarations.stub.models.ApiHeaders
import uk.gov.hmrc.customs.declarations.stub.repositories._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mongo.BSONBuilderHelpers
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.wco.dec.MetaData

@Singleton
class DeclarationStubController @Inject()(
  cc: ControllerComponents,
  auth: AuthConnector,
  clientRepo: ClientRepository,
  notificationConnector: NotificationConnector
)(implicit val appConfig: AppConfig, ec: ExecutionContext)
    extends BackendController(cc) with AuthorisedFunctions with BSONBuilderHelpers with Logging {

  private val permissibleAcceptHeaders: Set[String] =
    Set("application/vnd.hmrc.1.0+xml", "application/vnd.hmrc.2.0+xml", "application/vnd.hmrc.3.0+xml")

  private val permissibleContentTypes: Seq[String] =
    Seq(MimeTypes.XML, MimeTypes.XML + ";charset=utf-8", MimeTypes.XML + "; charset=utf-8")

  private val submitSchemas = Seq("/schemas/DocumentMetaData_2_DMS.xsd", "/schemas/WCO_DEC_2_DMS.xsd")

  private val cancelSchemas = Seq("/schemas/CANCEL_METADATA.xsd", "/schemas/CANCEL.xsd")

  private val lastSubmission: AtomicReference[Option[NodeSeq]] = new AtomicReference[Option[NodeSeq]](None)

  override def authConnector: AuthConnector = auth

  def submit(): Action[NodeSeq] = Action.async(parse.xml) { implicit request =>
    validateHeaders() { headers =>
      authenticate(headers) { client =>
        validatePayload(submitSchemas) { meta =>
          lastSubmission.set(Some(request.body))

          val conversationId = UUID.randomUUID().toString

          notificationConnector.notifyInDueCourse("submit", client, meta, conversationId = conversationId)

          Future.successful(Accepted.withHeaders("X-Conversation-ID" -> conversationId).as(ContentTypes.XML))
        }
      }
    }
  }

  def lastSubmit(): Action[AnyContent] = Action { _ =>
    lastSubmission.get().fold[Result](NotFound)(Ok(_)).as(ContentTypes.XML)
  }

  def submitNoNotification(): Action[NodeSeq] = Action.async(parse.xml) { implicit req =>
    validateHeaders() { headers =>
      authenticate(headers) { _ =>
        validatePayload(submitSchemas) { _ =>
          lastSubmission.set(Some(req.body))

          val conversationId = UUID.randomUUID().toString

          Future.successful(Accepted.withHeaders("X-Conversation-ID" -> conversationId).as(ContentTypes.XML))
        }
      }
    }
  }

  def cancel(): Action[NodeSeq] = Action.async(parse.xml) { implicit req =>
    validateHeaders() { headers =>
      authenticate(headers) { client =>
        validatePayload(cancelSchemas) { meta =>
          val conversationId = UUID.randomUUID().toString

          notificationConnector.notifyInDueCourse("cancel", client, meta, conversationId = conversationId)

          Future.successful(Accepted.withHeaders("X-Conversation-ID" -> conversationId).as(ContentTypes.XML))
        }
      }
    }
  }

  def cancelNoNotification(): Action[NodeSeq] = Action.async(parse.xml) { implicit req =>
    validateHeaders() { headers =>
      authenticate(headers) { _ =>
        validatePayload(cancelSchemas) { _ =>
          val conversationId = UUID.randomUUID().toString

          Future.successful(Accepted.withHeaders("X-Conversation-ID" -> conversationId).as(ContentTypes.XML))
        }
      }
    }
  }

  def listClients(): Action[AnyContent] = Action.async { _ =>
    clientRepo.findAll().map(found => Ok(Json.toJson(found ++ Seq(appConfig.defaultClient))))
  }

  def addClient(): Action[ClientWrapper] = Action.async(parse.json[ClientWrapper]) { implicit req =>
    val client = req.body.toClient
    clientRepo
      .findAndUpdate(
        Json.obj("clientId" -> client.clientId),
        Json.obj("clientId" -> client.clientId, "callbackUrl" -> client.callbackUrl, "token" -> client.token)
      )
      .map(res => if (res.value.isDefined) Created else InternalServerError)
  }

  def deleteClient(id: String): Action[AnyContent] = Action.async { _ =>
    clientRepo.removeById(BSONObjectID.parse(id).get).map(_ => NoContent)
  }

  // a dirty approximation of the header validation process implemented by customs declarations API
  private def validateHeaders()(f: ApiHeaders => Future[Result])(implicit req: Request[NodeSeq]): Future[Result] = {
    val accept = req.headers.get(HeaderNames.ACCEPT)
    val contentType = req.headers.get(HeaderNames.CONTENT_TYPE)
    val clientId = req.headers.get("X-Client-ID")
    val badgeId = req.headers.get("X-Badge-Identifier")

    if (!permissibleAcceptHeaders.contains(accept.get.toLowerCase)) Future.successful(NotAcceptable)
    else if (!permissibleContentTypes.contains(contentType.get.toLowerCase)) Future.successful(UnsupportedMediaType)
    else if (clientId.isEmpty) Future.successful(InternalServerError)
    else f(ApiHeaders(accept.get, contentType.get, clientId.get, badgeId))
  }

  private def authenticate(
    headers: ApiHeaders
  )(f: Client => Future[Result])(implicit hc: HeaderCarrier): Future[Result] =
    try {
      // we only care about whether the client request can be authorised via the implicit header carrier
      authorised(Enrolment("HMRC-CUS-ORG")) {
        clientRepo.findByClientId(headers.clientId).flatMap { maybeClient =>
          val client = maybeClient.getOrElse {
            if (headers.clientId == appConfig.defaultClient.clientId) appConfig.defaultClient
            else throw new IllegalArgumentException(s"Unauthorized client: ${headers.clientId}")
          }
          f(client)
        }
      }
    } catch {
      case _: Exception => Future.successful(Unauthorized)
    }

  private def validatePayload(
    schemas: Seq[String]
  )(f: MetaData => Future[Result])(implicit req: Request[NodeSeq]): Future[Result] = {
    val schema: Schema = {
      val sources = schemas
        .map(res => getClass.getResource(res).toString)
        .map(systemId => new StreamSource(systemId))
        .toArray[XmlSource]

      SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(sources)
    }

    val xml = req.body.mkString
    val validator = schema.newValidator()

    logger.debug(s"Payload received:\n$xml")

    val result = for {
      _ <- Try(validator.validate(new StreamSource(new StringReader(xml))))
      r <- Try(f(MetaData.fromXml(xml)))
    } yield r

    def logAndRespond(message: String, throwable: Throwable): Future[Result] = {
      logger.warn(message, throwable)
      Future.successful(BadRequest)
    }

    result.recover {
      case ex: SAXException =>
        logAndRespond(s"Invalid XML: ${ex.getMessage}\n$xml", ex)

      case ex: IOException =>
        logAndRespond(s"Invalid XML: ${ex.getMessage}\n$xml", ex)

      case ex: Exception =>
        logAndRespond(s"Cannot deserialize XML: ${ex.getMessage}", ex)
    }.get
  }
}
