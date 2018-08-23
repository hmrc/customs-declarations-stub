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

import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.Eventually
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.customs.test.CustomsPlaySpec
import TestData._
import scala.concurrent.Future

class PushClientNotificationServiceSpec extends CustomsPlaySpec with MockitoSugar with Eventually with BeforeAndAfterEach  with ClientTestData{
  private val mockPushNotificationServiceConnector = mock[PushNotificationServiceConnector]

  private val pushService = new PushClientNotificationService(mockPushNotificationServiceConnector)

  override protected def beforeEach(): Unit = reset(mockPushNotificationServiceConnector)
  var headercarrier = hc.withExtraHeaders("X-Client-ID"-> "eaca01f9-ec3b-4ede-b263-61b626dde231","X-Badge-Identifier"-> "1234")
  val reqHeaders = Map("X-Client-ID"-> pnrOne.clientSubscriptionId,"X-Badge-Identifier"-> "1234", "X-Conversation-ID" -> pnrOne.body.conversationId)


  "PushClientNotificationService" should {
    "return true when push is successful" in {
      when(mockPushNotificationServiceConnector.send(pnrOne)).thenReturn(Future.successful(()))

      val result = pushService.send(reqHeaders)(headercarrier)

      result must be (true)
      eventually(verify(mockPushNotificationServiceConnector).send(meq(pnrOne)))
    }

    "return false when push fails" in {
      when(mockPushNotificationServiceConnector.send(pnrOne)).thenReturn(Future.failed(emulatedServiceFailure))

      val result = pushService.send(reqHeaders)(headercarrier)
      result must be (false)
    }

  }

}
