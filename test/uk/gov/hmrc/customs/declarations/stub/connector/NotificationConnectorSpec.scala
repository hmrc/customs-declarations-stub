/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.customs.declarations.stub.connector

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalTo, post, postRequestedFor, urlEqualTo}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{doAnswer, spy}
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api
import play.api.Application
import play.api.http.HeaderNames
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.customs.declarations.stub.base.UnitTestSpec
import uk.gov.hmrc.customs.declarations.stub.models.Client
import uk.gov.hmrc.http.test.{HttpClientV2Support, WireMockSupport}
import uk.gov.hmrc.wco.dec.{Declaration, MetaData}
import play.api.http.Status._
import uk.gov.hmrc.http.client.HttpClientV2

import java.util.UUID
import scala.concurrent.duration.{Duration, FiniteDuration}

class NotificationConnectorSpec extends UnitTestSpec with GuiceOneAppPerSuite with BeforeAndAfterAll with WireMockSupport with HttpClientV2Support {

  val spyHttpClient: HttpClientV2 = spy(httpClientV2)

  val appBuilder: GuiceApplicationBuilder = GuiceApplicationBuilder()
    .overrides(
      api.inject.bind[HttpClientV2].toInstance(spyHttpClient),
    )
    .configure(Map[String, Any](
      "microservice.services.client.host" -> wireMockHost,
      "microservice.services.client.port" -> wireMockPort,
      "microservice.services.client.uri" -> "/customs-declare-exports/notify",
      "microservice.services.client.token" -> "token"
    ))

  val notificationConnector: NotificationConnector = appBuilder.injector().instanceOf[NotificationConnector]

  private lazy val connector: NotificationConnector = spy(notificationConnector)

  override implicit lazy val app: Application = appBuilder.overrides(api.inject.bind[NotificationConnector].toInstance(connector)).build()

  "NotificationConnector" should {

    "return Accepted and call use the default notification Body" in {
      val client = Client("clientId", s"http://$wireMockHost:$wireMockPort/customs-declare-exports/notify", "token")

      val metaData: MetaData = MetaData(declaration = Some(Declaration(functionalReferenceId = Some("BLRN"), typeCode = Some("ABC"))))

      var capturedReturn: (FiniteDuration, String, Option[String]) = null
      doAnswer(invocation => {
        val result = invocation.callRealMethod().asInstanceOf[(FiniteDuration, String, Option[String])]
        capturedReturn = result
        result
      }).when(connector).generate(any(), any(), any())

      returnResponseForRequest()

      val conversationId = UUID.randomUUID.toString
      val result: Unit = connector.notifyInDueCourse("submit", conversationId, None, client, metaData, Duration(100, "ms"))

      Thread.sleep(1000)

      val actualPayload = if(capturedReturn._2.trim.nonEmpty) capturedReturn._2 else capturedReturn._3.get

      result shouldBe ((): Unit)
      wireMockServer.verify(postRequestedFor(urlEqualTo("/customs-declare-exports/notify"))
        .withRequestBody(equalTo(actualPayload))
        .withHeader(HeaderNames.CONTENT_TYPE, equalTo("application/xml; charset=utf-8"))
        .withHeader(HeaderNames.AUTHORIZATION, equalTo("Bearer token"))
        .withHeader("X-Conversation-ID", equalTo(conversationId)))
    }
  }

  private def returnResponseForRequest(): Unit =
    wireMockServer.stubFor(post(urlEqualTo("/customs-declare-exports/notify"))
      willReturn aResponse()
      .withStatus(OK))
}
