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

package services

import java.util.UUID

import com.typesafe.config.{Config, ConfigFactory}
import model._
import org.joda.time.DateTime
import play.api.http.HeaderNames._
import play.api.http.MimeTypes
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.AnyContentAsXml
import services.TestData._
import play.api.test.FakeRequest
import scala.xml.{Elem, NodeSeq}

object TestData extends RequestHeaders{

  val validConversationId: String = "eaca01f9-ec3b-4ede-b263-61b626dde232"
  val validConversationIdUUID = UUID.fromString(validConversationId)
  val conversationId = ConversationId(validConversationIdUUID)
  val invalidConversationId: String = "I-am-not-a-valid-uuid"

  val validFieldsId = "ffff01f9-ec3b-4ede-b263-61b626dde232"
  val someFieldsId = "ccc9f676-c752-4e77-b86a-b27a3b33fceb"
  val clientSubscriptionId = ClientSubscriptionId(UUID.fromString(validFieldsId))
  val invalidFieldsId = "I-am-not-a-valid-type-4-uuid"

  val basicAuthTokenValue = "YmFzaWN1c2VyOmJhc2ljcGFzc3dvcmQ="
  val validBasicAuthToken = s"Basic $basicAuthTokenValue"
  val invalidBasicAuthToken = "I-am-not-a-valid-auth-token"
  val overwrittenBasicAuthToken = "value-not-logged"

  type EmulatedServiceFailure = UnsupportedOperationException
  val emulatedServiceFailure = new EmulatedServiceFailure("Emulated service failure.")

  val callbackUrl = "http://callback"
  val invalidCallbackUrl = "Im-Invalid"
  val securityToken = "securityToken"
  val callbackData = DeclarantCallbackData(callbackUrl, securityToken)
  val invalidCallbackData = DeclarantCallbackData(invalidCallbackUrl, securityToken)

  val url = "http://some-url"
  val errorMsg = "ERROR"
  val warnMsg = "WARN"
  val infoMsg = "INFO"
  val debugMsg = "DEBUG"

  val badgeId = "ABCDEF1234"
  val userAgent = "Customs Declaration Service"

  lazy val somePushNotificationRequest: Option[PushNotificationRequest] = Some(pushNotificationRequest)
  lazy val pushNotificationRequest: PushNotificationRequest = pushNotificationRequest(ValidXML)

  def clientNotification(withBadgeId: Boolean = true): ClientNotification = {

    val headers: Seq[Header] = if (withBadgeId) {
      Seq[Header](Header(X_BADGE_ID_HEADER_NAME, badgeId))
    } else Seq[Header]()

    ClientNotification(
      csid = clientSubscriptionId,
      Notification(
        conversationId = conversationId,
        headers = headers,
        payload = ValidXML.toString(),
        contentType = MimeTypes.XML
      )
    )
  }

  def createPushNotificationRequestPayload(outboundUrl: String = callbackData.callbackUrl, securityToken: String = callbackData.securityToken,
                                           mayBeBadgeId: Option[String] = Some(badgeId), notificationPayload: NodeSeq = ValidXML,
                                           conversationId: String = validConversationId): JsValue = Json.parse(
    s"""
       |{
       |   "url": "$outboundUrl",
       |   "conversationId": "$conversationId",
       |   "authHeaderToken": "$securityToken",
       |      "outboundCallHeaders": [""".stripMargin
      + mayBeBadgeId.fold("")(badge => s"""  {"name": "X-Badge-Identifier", "value": "$badge"}   """) +
      s"""
         |],
         |   "xmlPayload": "${notificationPayload.toString()}"
         |}
    """.stripMargin)

  def pushNotificationRequest(xml: NodeSeq): PushNotificationRequest = {
    val body = PushNotificationRequestBody(callbackData.callbackUrl, callbackData.securityToken, validConversationId, Seq(Header(X_BADGE_ID_HEADER_NAME, badgeId)), xml.toString())
    PushNotificationRequest(validFieldsId, body)
  }

  def failedPushNotificationRequest(xml: NodeSeq): PushNotificationRequest = {
    val body = PushNotificationRequestBody(invalidCallbackData.callbackUrl, callbackData.securityToken, validConversationId, Seq(Header(X_BADGE_ID_HEADER_NAME, badgeId)), xml.toString())
    PushNotificationRequest(validFieldsId, body)
  }

  val ValidXML: Elem = <Foo>Bar</Foo>

  lazy val ValidRequest: FakeRequest[AnyContentAsXml] = FakeRequest()
    .withHeaders(X_CDS_CLIENT_ID_HEADER, X_CONVERSATION_ID_HEADER, CONTENT_TYPE_HEADER, ACCEPT_HEADER, BASIC_AUTH_HEADER, X_BADGE_ID_HEADER)
    .withXmlBody(ValidXML)

  lazy val ValidRequestWithClientIdAbsentInDatabase: FakeRequest[AnyContentAsXml] = FakeRequest()
    .withHeaders(X_ABSENT_CDS_CLIENT_ID_HEADER, X_CONVERSATION_ID_HEADER, CONTENT_TYPE_HEADER, ACCEPT_HEADER, BASIC_AUTH_HEADER, X_BADGE_ID_HEADER)
    .withXmlBody(ValidXML)

  lazy val InvalidConversationIdHeaderRequest: FakeRequest[AnyContentAsXml] = FakeRequest()
    .withHeaders(X_CDS_CLIENT_ID_HEADER, X_CONVERSATION_ID_INVALID, CONTENT_TYPE_HEADER, ACCEPT_HEADER)
    .withXmlBody(ValidXML)

  lazy val MissingConversationIdHeaderRequest: FakeRequest[AnyContentAsXml] = FakeRequest()
    .withHeaders(X_CDS_CLIENT_ID_HEADER, CONTENT_TYPE_HEADER, ACCEPT_HEADER)
    .withXmlBody(ValidXML)

  lazy val InvalidAuthorizationHeaderRequest: FakeRequest[AnyContentAsXml] = FakeRequest()
    .withHeaders(X_CDS_CLIENT_ID_HEADER, X_CONVERSATION_ID_HEADER, CONTENT_TYPE_HEADER, ACCEPT_HEADER, BASIC_AUTH_HEADER_INVALID)
    .withXmlBody(ValidXML)

  lazy val MissingAuthorizationHeaderRequest: FakeRequest[AnyContentAsXml] = FakeRequest()
    .withHeaders(X_CDS_CLIENT_ID_HEADER, CONTENT_TYPE_HEADER, ACCEPT_HEADER, X_CONVERSATION_ID_HEADER)
    .withXmlBody(ValidXML)

  lazy val InvalidClientIdHeaderRequest: FakeRequest[AnyContentAsXml] = FakeRequest()
    .withHeaders(X_CDS_CLIENT_ID_INVALID, X_CONVERSATION_ID_HEADER, CONTENT_TYPE_HEADER, ACCEPT_HEADER)
    .withXmlBody(ValidXML)

  lazy val MissingClientIdHeaderRequest: FakeRequest[AnyContentAsXml] = FakeRequest()
    .withHeaders(X_CONVERSATION_ID_INVALID, CONTENT_TYPE_HEADER, ACCEPT_HEADER)
    .withXmlBody(ValidXML)

  lazy val InvalidContentTypeHeaderRequest: FakeRequest[AnyContentAsXml] = FakeRequest()
    .withHeaders(X_CDS_CLIENT_ID_HEADER, X_CONVERSATION_ID_HEADER, CONTENT_TYPE_HEADER_INVALID, ACCEPT_HEADER)
    .withXmlBody(ValidXML)

  lazy val MissingAcceptHeaderRequest: FakeRequest[AnyContentAsXml] = FakeRequest()
    .withHeaders(X_CDS_CLIENT_ID_HEADER, X_CONVERSATION_ID_HEADER, CONTENT_TYPE_HEADER)
    .withXmlBody(ValidXML)

  lazy val InvalidAcceptHeaderRequest: FakeRequest[AnyContentAsXml] = FakeRequest()
    .withHeaders(X_CDS_CLIENT_ID_HEADER, X_CONVERSATION_ID_HEADER, CONTENT_TYPE_HEADER, ACCEPT_HEADER_INVALID)
    .withXmlBody(ValidXML)

  val errorResponseForMissingAcceptHeader: Elem =
    <errorResponse>
      <code>ACCEPT_HEADER_INVALID</code>
      <message>The accept header is missing or invalid</message>
    </errorResponse>

  val errorResponseForInvalidContentType: Elem =
    <errorResponse>
      <code>UNSUPPORTED_MEDIA_TYPE</code>
      <message>The content type header is missing or invalid</message>
    </errorResponse>

  val errorResponseForUnauthorized: Elem =
    <errorResponse>
      <code>UNAUTHORIZED</code>
      <message>Basic token is missing or not authorized</message>
    </errorResponse>

  val errorResponseForInvalidClientId: Elem =
    <errorResponse>
      <code>BAD_REQUEST</code>
      <message>The X-CDS-Client-ID header value is invalid</message>
    </errorResponse>

  val errorResponseForMissingClientId: Elem =
    <errorResponse>
      <code>BAD_REQUEST</code>
      <message>The X-CDS-Client-ID header is missing</message>
    </errorResponse>

  val errorResponseForInvalidConversationId: Elem =
    <errorResponse>
      <code>BAD_REQUEST</code>
      <message>The X-Conversation-ID header value is invalid</message>
    </errorResponse>

  val errorResponseForMissingConversationId: Elem =
    <errorResponse>
      <code>BAD_REQUEST</code>
      <message>The X-Conversation-ID header is missing</message>
    </errorResponse>

  val errorResponseForPlayXmlBodyParserError: Elem =
    <errorResponse>
      <code>BAD_REQUEST</code>
      <message>Invalid XML: Premature end of file.</message>
    </errorResponse>

  val errorResponseForClientIdNotFound: Elem = errorResponseForInvalidClientId

  lazy val invalidConfigMissingBasicAuthToken: Config = ConfigFactory.parseString("")
}



trait RequestHeaders {
  val X_CDS_CLIENT_ID_HEADER_NAME: String = "X-CDS-Client-ID"

  val SUBSCRIPTION_FIELDS_ID_HEADER_NAME: String = "api-subscription-fields-id"

  val X_CONVERSATION_ID_HEADER_NAME: String = "X-Conversation-ID"

  val X_BADGE_ID_HEADER_NAME : String = "X-Badge-Identifier"
  lazy val X_CDS_CLIENT_ID_HEADER: (String, String) = X_CDS_CLIENT_ID_HEADER_NAME -> validFieldsId

  lazy val X_ABSENT_CDS_CLIENT_ID_HEADER: (String, String) = X_CDS_CLIENT_ID_HEADER_NAME -> someFieldsId

  lazy val X_CDS_CLIENT_ID_INVALID: (String, String) = X_CDS_CLIENT_ID_HEADER_NAME -> invalidFieldsId

  lazy val X_CONVERSATION_ID_HEADER: (String, String) = X_CONVERSATION_ID_HEADER_NAME -> validConversationId

  lazy val X_CONVERSATION_ID_INVALID: (String, String) = X_CONVERSATION_ID_HEADER_NAME -> invalidConversationId

  lazy val X_BADGE_ID_HEADER: (String, String) = X_BADGE_ID_HEADER_NAME -> badgeId
  val XmlCharsetUtf8: String = MimeTypes.XML + "; charset=UTF-8"
  lazy val CONTENT_TYPE_HEADER: (String, String) = CONTENT_TYPE -> XmlCharsetUtf8

  lazy val CONTENT_TYPE_HEADER_LOWERCASE: (String, String) = CONTENT_TYPE -> XmlCharsetUtf8.toLowerCase

  lazy val CONTENT_TYPE_HEADER_INVALID: (String, String) = CONTENT_TYPE -> MimeTypes.BINARY

  lazy val ACCEPT_HEADER: (String, String) = ACCEPT -> MimeTypes.XML

  lazy val ACCEPT_HEADER_INVALID: (String, String) = ACCEPT -> MimeTypes.BINARY

  lazy val BASIC_AUTH_HEADER: (String, String) = AUTHORIZATION -> validBasicAuthToken

  lazy val BASIC_AUTH_HEADER_INVALID: (String, String) = AUTHORIZATION -> invalidBasicAuthToken

  lazy val BASIC_AUTH_HEADER_OVERWRITTEN: (String, String) = AUTHORIZATION -> overwrittenBasicAuthToken

  lazy val ValidHeaders = Map(
    X_CDS_CLIENT_ID_HEADER,
    X_CONVERSATION_ID_HEADER,
    CONTENT_TYPE_HEADER,
    ACCEPT_HEADER,
    BASIC_AUTH_HEADER,
    X_BADGE_ID_HEADER
  )

  val LoggingHeaders = Seq(X_CDS_CLIENT_ID_HEADER, X_CONVERSATION_ID_HEADER)
  val LoggingHeadersWithAuth = Seq(X_CDS_CLIENT_ID_HEADER, X_CONVERSATION_ID_HEADER, BASIC_AUTH_HEADER)
  val LoggingHeadersWithAuthOverwritten = Seq(X_CDS_CLIENT_ID_HEADER, X_CONVERSATION_ID_HEADER, BASIC_AUTH_HEADER_OVERWRITTEN)

  val NoHeaders: Map[String, String] = Map[String, String]()
}
trait ClientTestData {
  val CsidOne = ClientSubscriptionId(UUID.fromString("eaca01f9-ec3b-4ede-b263-61b626dde231"))
  val CsidTwo = ClientSubscriptionId(UUID.fromString("eaca01f9-ec3b-4ede-b263-61b626dde232"))
  val ConversationIdOne = ConversationId(UUID.fromString("caca01f9-ec3b-4ede-b263-61b626dde231"))
  val ConversationIdTwo = ConversationId(UUID.fromString("caca01f9-ec3b-4ede-b263-61b626dde232"))
 // val CsidOneLockOwnerId = LockOwnerId(CsidOne.id.toString)

  val Headers = Seq(Header("h1", "v1"))
  val PayloadOne = "PAYLOAD_ONE"
  val PayloadTwo = "PAYLOAD_TWO"
  val ContentType = "CONTENT_TYPE"
  val NotificationOne = Notification(ConversationIdOne, Headers, PayloadOne, ContentType)
  val NotificationTwo = Notification(ConversationIdTwo, Headers, PayloadTwo, ContentType)
  val TimeStampOne = DateTime.now
  private val oneThousand = 1000
  val TimeStampTwo = TimeStampOne.plus(oneThousand)
  val ClientNotificationOne = ClientNotification(CsidOne, NotificationOne, Some(TimeStampOne))
  val ClientNotificationTwo = ClientNotification(CsidOne, NotificationTwo, Some(TimeStampTwo))

  val DeclarantCallbackDataOne = DeclarantCallbackData("URL", "SECURITY_TOKEN")
  val DeclarantCallbackDataOneWithEmptyUrl = DeclarantCallbackDataOne.copy(callbackUrl = "")
  val DeclarantCallbackDataTwo = DeclarantCallbackData("URL2", "SECURITY_TOKEN2")
  val pnrOne = PushNotificationRequest(CsidOne.id.toString, PushNotificationRequestBody("URL", "SECURITY_TOKEN", ConversationIdOne.id.toString, Headers, PayloadOne))
  val pnrTwo = PushNotificationRequest(CsidOne.id.toString, PushNotificationRequestBody("URL2", "SECURITY_TOKEN2", ConversationIdTwo.id.toString, Headers, PayloadTwo))
}