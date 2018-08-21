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

package model

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.json._
import uk.gov.hmrc.customs.api.common.controllers.ErrorResponse


sealed trait ApiVersion {
  val value: String
  val configPrefix: String
  override def toString: String = value
}
object VersionOne extends ApiVersion{
  override val value: String = "1.0"
  override val configPrefix: String = ""
}
object VersionTwo extends ApiVersion{
  override val value: String = "2.0"
  override val configPrefix: String = "v2."
}
object VersionThree extends ApiVersion{
  override val value: String = "3.0"
  override val configPrefix: String = "v3."
}

case class ConversationId(id: UUID) extends AnyVal {
  override def toString(): String = id.toString
}

object ConversationId {
  implicit val conversationIdJF = new Format[ConversationId] {
    def writes(conversationId: ConversationId) = JsString(conversationId.id.toString)
    def reads(json: JsValue) = json match {
      case JsNull => JsError()
      case _ => JsSuccess(ConversationId(json.as[UUID]))
    }
  }
}
case class Header(name: String, value: String)

object Header {
  implicit val jsonFormat: OFormat[Header] = Json.format[Header]
}

case class DeclarantCallbackData(callbackUrl: String, securityToken: String)
object DeclarantCallbackData {
  implicit val jsonFormat = Json.format[DeclarantCallbackData]
}

case class ClientSubscriptionId(id: UUID) extends AnyVal {
  override def toString(): String = id.toString
}
object ClientSubscriptionId {
  implicit val clientSubscriptionIdJF = new Format[ClientSubscriptionId] {
    def writes(csid: ClientSubscriptionId) = JsString(csid.id.toString)
    def reads(json: JsValue) = json match {
      case JsNull => JsError()
      case _ => JsSuccess(ClientSubscriptionId(json.as[UUID]))
    }
  }
}


case class Notification(conversationId: ConversationId, headers: Seq[Header], payload: String, contentType: String)
object Notification {
  implicit val notificationJF = Json.format[Notification]
}
case class ClientNotification(csid: ClientSubscriptionId,
                              notification: Notification,
                              timeReceived: Option[DateTime] = None,
                              id: UUID = UUID.randomUUID())

object ClientNotification {
  implicit val clientNotificationJF = Json.format[ClientNotification]
}

case class PushNotificationRequestBody(
                                        url: String,
                                        authHeaderToken: String,
                                        conversationId: String,
                                        outboundCallHeaders: Seq[Header],
                                        xmlPayload: String
                                        )

object PushNotificationRequestBody {
  implicit val jsonFormat: OFormat[PushNotificationRequestBody] = Json.format[PushNotificationRequestBody]
}
case class PushNotificationRequest(
                                    clientSubscriptionId: String,
                                    body: PushNotificationRequestBody
                                    )
