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

package uk.gov.hmrc.customs.declarations.stub.config

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.customs.declarations.stub.models.Client
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AppConfig @Inject() (runModeConfiguration: Configuration, servicesConfig: ServicesConfig) {

  private def loadConfig(key: String): String =
    runModeConfiguration.getOptional[String](key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  val defaultClient: Client = Client(
    clientId = loadConfig("microservice.services.client.id"),
    callbackUrl = servicesConfig.baseUrl("client") + loadConfig("microservice.services.client.uri"),
    token = loadConfig("microservice.services.client.token")
  )

  val cdsFileUploadFrontendPublicBaseUrl = servicesConfig.baseUrl("cds-file-upload-frontend-public")
  val cdsFileUploadFrontendInternalBaseUrl = servicesConfig.baseUrl("cds-file-upload-frontend-internal")
}
