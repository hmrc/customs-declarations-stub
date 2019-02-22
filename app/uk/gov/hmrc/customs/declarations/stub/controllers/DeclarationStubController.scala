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

import java.io.StringReader
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import javax.inject.{Inject, Singleton}
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.transform.{Source => XmlSource}
import javax.xml.validation.{Schema, SchemaFactory}
import play.api.Logger
import play.api.http.{ContentTypes, HeaderNames, MimeTypes}
import play.api.libs.json.Json
import play.api.mvc._
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions, Enrolment}
import uk.gov.hmrc.customs.declarations.stub.config.AppConfig
import uk.gov.hmrc.customs.declarations.stub.connector.NotificationConnector
import uk.gov.hmrc.customs.declarations.stub.models.ApiHeaders
import uk.gov.hmrc.customs.declarations.stub.repositories._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mongo.BSONBuilderHelpers
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.wco.dec.MetaData
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.NodeSeq

@Singleton
class DeclarationStubController @Inject()(
  auth: AuthConnector,
  clientRepo: ClientRepository,
  notificationConnector: NotificationConnector
)(implicit val appConfig: AppConfig, ec: ExecutionContext) extends BaseController with AuthorisedFunctions with BSONBuilderHelpers {

  private val permissibleAcceptHeaders: Set[String] =
    Set("application/vnd.hmrc.1.0+xml", "application/vnd.hmrc.2.0+xml", "application/vnd.hmrc.3.0+xml")

  private val permissibleContentTypes: Seq[String] = Seq(MimeTypes.XML, MimeTypes.XML + ";charset=utf-8", MimeTypes.XML + "; charset=utf-8")

  private val submitSchemas = Seq("/schemas/DocumentMetaData_2_DMS.xsd", "/schemas/WCO_DEC_2_DMS.xsd")

  private val cancelSchemas = Seq("/schemas/CANCEL_METADATA.xsd", "/schemas/CANCEL.xsd")


  private var lastSubmission: AtomicReference[Option[NodeSeq]] = new AtomicReference[Option[NodeSeq]](None)

  override def authConnector: AuthConnector = auth

  def submit(): Action[NodeSeq] = Action.async(parse.xml) { implicit req =>
    validateHeaders() { hdrs =>
      authenticate(hdrs) { client =>
        validatePayload(submitSchemas) { meta =>

          lastSubmission.set(Some(req.body))

          val conversationId = UUID.randomUUID().toString
          notificationConnector.notifyInDueCourse("submit", hdrs, client, meta, conversationId = conversationId)
          Future.successful(Accepted.withHeaders("X-Conversation-ID" -> conversationId).as(ContentTypes.XML))
        }
      }
    }
  }

  def lastSubmit(): Action[AnyContent] = Action { implicit req =>
    lastSubmission.get().fold[Result](NotFound)(Ok(_)).as(ContentTypes.XML)
  }

  def submitNoNotification(): Action[NodeSeq] = Action.async(parse.xml) { implicit req =>
    validateHeaders() { hdrs =>
      authenticate(hdrs) { client =>
        validatePayload(submitSchemas) { meta =>

          lastSubmission.set(Some(req.body))

          val conversationId = UUID.randomUUID().toString
          Future.successful(Accepted.withHeaders("X-Conversation-ID" -> conversationId).as(ContentTypes.XML))
        }
      }
    }
  }

  def cancel(): Action[NodeSeq] = Action.async(parse.xml) { implicit req =>
    validateHeaders() { hdrs =>
      authenticate(hdrs) { client =>
        validatePayload(cancelSchemas) { meta =>
          val conversationId = UUID.randomUUID().toString
          notificationConnector.notifyInDueCourse("cancel", hdrs, client, meta, conversationId = conversationId)
          Future.successful(Accepted.withHeaders("X-Conversation-ID" -> conversationId).as(ContentTypes.XML))
        }
      }
    }
  }

  def cancelNoNotification(): Action[NodeSeq] = Action.async(parse.xml) { implicit req =>
    validateHeaders() { hdrs =>
      authenticate(hdrs) { client =>
        validatePayload(cancelSchemas) { meta =>
          val conversationId = UUID.randomUUID().toString
          Future.successful(Accepted.withHeaders("X-Conversation-ID" -> conversationId).as(ContentTypes.XML))
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
      case e: Exception =>
        Logger.warn(s"Invalid XML: ${e.getMessage}\n$xml", e)
        return Future.successful(BadRequest)
    }

    try {
      f(MetaData.fromXml(xml)) // additionally, catch failure to deserialize as WCO domain case class for the sake of visibility on this kind of error
    } catch {
      case e: Exception =>
        Logger.warn(s"Cannot deserialize XML: ${e.getMessage}", e)
        Future.successful(BadRequest)
    }
  }
}
