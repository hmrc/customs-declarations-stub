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

package uk.gov.hmrc.customs.declarations.stub.base

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterEach, Suite}
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.customs.declarations.stub.controllers.actions.AuthActionImpl
import uk.gov.hmrc.customs.declarations.stub.models.requests.SignedInUser
import uk.gov.hmrc.customs.declarations.stub.testdata.CommonTestData.{eori, signedInUser}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.global
import scala.concurrent.{ExecutionContext, Future}

trait AuthConnectorMock extends BeforeAndAfterEach with MockitoSugar { self: Suite =>

  val authConnectorMock: AuthConnector = mock[AuthConnector]

  val authActionMock = new AuthActionImpl(authConnectorMock, stubControllerComponents())(global)

  def authorizedUser: Unit =
    when(authConnectorMock.authorise(any[Predicate], any[Retrieval[Unit]])(any[HeaderCarrier], any[ExecutionContext]))
      .thenReturn(Future.successful(()))

  def userWithEori(user: SignedInUser = signedInUser(eori)): Unit =
    when(authConnectorMock.authorise(any(), any[Retrieval[Enrolments]])(any(), any()))
      .thenReturn(Future.successful(user.enrolments))

  def userWithoutEori: Unit =
    when(authConnectorMock.authorise(any(), any[Retrieval[Enrolments]])(any(), any()))
      .thenReturn(Future.successful(Enrolments(Set())))

  override def afterEach(): Unit = {
    reset(authConnectorMock)
    super.afterEach()
  }
}
