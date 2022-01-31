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

package uk.gov.hmrc.customs.declarations.stub.controllers.actions

import com.google.inject.{ImplementedBy, Inject}
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals._
import uk.gov.hmrc.customs.declarations.stub.models.requests.{AuthenticatedRequest, SignedInUser}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class AuthActionImpl @Inject()(override val authConnector: AuthConnector, cc: ControllerComponents)(
  implicit override val executionContext: ExecutionContext
) extends AuthAction with AuthorisedFunctions {

  override val parser: BodyParser[AnyContent] = cc.parsers.defaultBodyParser

  private val hmrcCusOrgEnrolmentKey = "HMRC-CUS-ORG"
  private val eoriNumberIdentifierKey = "EORINumber"

  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

    authorised(Enrolment(hmrcCusOrgEnrolmentKey))
      .retrieve(allEnrolments) { allEnrolments: Enrolments =>
        val eori = getEoriEnrolment(allEnrolments)

        if (eori.isEmpty) {
          Future.successful(Results.Unauthorized)
        } else {
          val cdsLoggedInUser = SignedInUser(eori.get.value, allEnrolments)
          block(AuthenticatedRequest(request, cdsLoggedInUser))
        }
      }
  }

  private def getEoriEnrolment(enrolments: Enrolments): Option[EnrolmentIdentifier] =
    enrolments.getEnrolment(hmrcCusOrgEnrolmentKey).flatMap(_.getIdentifier(eoriNumberIdentifierKey))
}

@ImplementedBy(classOf[AuthActionImpl])
trait AuthAction extends ActionBuilder[AuthenticatedRequest, AnyContent]
