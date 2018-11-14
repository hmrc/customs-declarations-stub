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

package repositories

import base.CustomsDeclarationsStubBase
import models.Notification
import org.scalatest.BeforeAndAfterAll
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.wco.dec.{Declaration, MetaData}

import scala.reflect.ClassTag

class NotificationRepositorySpec extends CustomsDeclarationsStubBase with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    notificationRepository.removeAll().futureValue
    notificationRepository.insert(Notification(clientId, operation, lrn, xml)).futureValue
  }

  override lazy val app: Application = GuiceApplicationBuilder().build()

  protected def component[T: ClassTag]: T = app.injector.instanceOf[T]

  val notificationRepository = component[NotificationRepository]

  val clientId = "clientId"
  val operation = "operation"
  val lrn = "lrn"
  val xml = "xml"
  val declaration = Declaration(functionalReferenceId = Some(lrn))
  val metaData = MetaData(declaration = declaration)

  "Notification repository" should {
    "find by client and operation and metadata" in {
      val notification =
        notificationRepository.findByClientAndOperationAndMetaData(clientId, operation, metaData).futureValue.get

      notification.clientId must be(clientId)
      notification.operation must be(operation)
      notification.lrn must be(lrn)
      notification.xml must be(xml)
    }

    "find by client and operation and lrn" in {
      val notification = notificationRepository.findByClientAndOperationAndLrn(clientId, operation, lrn).futureValue.get

      notification.clientId must be(clientId)
      notification.operation must be(operation)
      notification.lrn must be(lrn)
      notification.xml must be(xml)
    }
  }
}
