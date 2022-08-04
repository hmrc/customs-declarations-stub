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

package uk.gov.hmrc.customs.declarations.stub.controllers

import org.xml.sax.SAXException
import play.api.Logging
import play.api.http.{ContentTypes, HeaderNames, MimeTypes}
import play.api.libs.json.Json
import play.api.mvc._
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions, Enrolment}
import uk.gov.hmrc.customs.declarations.stub.config.AppConfig
import uk.gov.hmrc.customs.declarations.stub.config.featureFlags.SchemaValidationConfig
import uk.gov.hmrc.customs.declarations.stub.connector.NotificationConnector
import uk.gov.hmrc.customs.declarations.stub.models.ApiHeaders
import uk.gov.hmrc.customs.declarations.stub.repositories._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mongo.BSONBuilderHelpers
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.wco.dec.MetaData

import java.io.{IOException, StringReader}
import java.util.UUID
import javax.inject.{Inject, Singleton}
import javax.xml.XMLConstants
import javax.xml.transform.{Source => XmlSource}
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.{Schema, SchemaFactory}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}
import scala.xml.NodeSeq

@Singleton
class DeclarationStubController @Inject()(
  cc: ControllerComponents,
  auth: AuthConnector,
  clientRepo: ClientRepository,
  notificationConnector: NotificationConnector,
  schemaValidationConfig: SchemaValidationConfig
)(implicit val appConfig: AppConfig, ec: ExecutionContext)
    extends BackendController(cc) with AuthorisedFunctions with BSONBuilderHelpers with Logging {

  private val permissibleAcceptHeaders: Set[String] =
    Set("application/vnd.hmrc.1.0+xml", "application/vnd.hmrc.2.0+xml", "application/vnd.hmrc.3.0+xml")

  private val permissibleContentTypes: Seq[String] =
    Seq(MimeTypes.XML, MimeTypes.XML + ";charset=utf-8", MimeTypes.XML + "; charset=utf-8")

  private val submitSchemas = Seq("/schemas/DocumentMetaData_2_DMS.xsd", "/schemas/WCO_DEC_2_DMS.xsd")

  private val cancelSchemas = Seq("/schemas/CANCEL_METADATA.xsd", "/schemas/CANCEL.xsd")

  override def authConnector: AuthConnector = auth

  logger.warn(s"Schema Validation enabled? ${schemaValidationConfig.isEnabled}")

  def submit(): Action[NodeSeq] = Action.async(parse.xml(maxLength = 1024 * 100000)) { implicit request =>
    validateHeaders() { headers =>
      authenticate(headers) { client =>
        validatePayload(submitSchemas) { meta =>
          val conversationId = UUID.randomUUID().toString

          notificationConnector
            .notifyInDueCourse("submit", client, meta, conversationId = conversationId, submissionConversationId = None)

          Future.successful(Accepted.withHeaders("X-Conversation-ID" -> conversationId).as(ContentTypes.XML))
        }
      }
    }
  }

  def cancel(): Action[NodeSeq] = Action.async(parse.xml) { implicit req =>
    validateHeaders() { headers =>
      authenticate(headers) { client =>
        validatePayload(cancelSchemas) { meta =>
          val cancellationConversationId = UUID.randomUUID().toString
          val submissionConversationId = req.headers.get("submissionConversationId")

          notificationConnector.notifyInDueCourse(
            "cancel",
            client,
            meta,
            conversationId = cancellationConversationId,
            submissionConversationId = submissionConversationId
          )

          Future.successful(
            Accepted.withHeaders("X-Conversation-ID" -> cancellationConversationId).as(ContentTypes.XML)
          )
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

    val xml = req.body.mkString
    logger.debug(s"Payload received:\n$xml")

    def validate(): Try[Unit] = {
      logger.debug(s"Schema Validation enabled? ${schemaValidationConfig.isEnabled}")
      if (!schemaValidationConfig.isEnabled)
        Success(())
      else {
        val schema: Schema = {
          val sources = schemas
            .map(res => getClass.getResource(res).toString)
            .map(systemId => new StreamSource(systemId))
            .toArray[XmlSource]

          SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(sources)
        }

        val validator = schema.newValidator()

        Try(validator.validate(new StreamSource(new StringReader(xml))))
      }
    }

    val result = for {
      _ <- validate()
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
