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
import models.Client
import org.scalatest.BeforeAndAfterAll
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

import scala.reflect.ClassTag

class ClientRepositorySpec extends CustomsDeclarationsStubBase with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    clientRepository.removeAll()
  }

  override lazy val app: Application = GuiceApplicationBuilder().build()

  protected def component[T: ClassTag]: T = app.injector.instanceOf[T]

  val clientRepository = component[ClientRepository]

  val clientId = "clientId"
  val callbackUrl = "callbackUrl"
  val token = "token"

  "Client repository" should {
    "find client by client id" in {
      clientRepository.insert(Client(clientId, callbackUrl, token)).futureValue

      val client = clientRepository.findByClientId(clientId).futureValue.get

      client.clientId must be(clientId)
      client.callbackUrl must be(callbackUrl)
      client.token must be(token)
    }
  }
}
