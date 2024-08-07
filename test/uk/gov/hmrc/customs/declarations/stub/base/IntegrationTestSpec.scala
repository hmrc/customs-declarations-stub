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

package uk.gov.hmrc.customs.declarations.stub.base

import org.apache.pekko.actor.ActorSystem
import org.mockito.MockitoSugar.{mock, reset}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.customs.declarations.stub.connector.NotificationConnector

import scala.reflect.ClassTag

trait IntegrationTestSpec extends UnitTestSpec with AuthConnectorMock with GuiceOneAppPerSuite {

  val notificationConnectorMock: NotificationConnector = mock[NotificationConnector]

  override implicit lazy val app: Application =
    GuiceApplicationBuilder()
      .overrides(bind[AuthConnector].to(authConnectorMock), bind[NotificationConnector].to(notificationConnectorMock))
      .build()

  implicit val actorSystem: ActorSystem = ActorSystem()

  override def beforeEach(): Unit =
    reset(notificationConnectorMock)

  def instanceOf[T](implicit classTag: ClassTag[T]): T = app.injector.instanceOf[T]
}
