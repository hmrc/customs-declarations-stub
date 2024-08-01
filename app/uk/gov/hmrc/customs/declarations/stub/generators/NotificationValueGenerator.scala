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

package uk.gov.hmrc.customs.declarations.stub.generators

import scala.util.Random

class NotificationValueGenerator {

  def generateMRN(): String = "18GBJ" + randomAlphaNumeric

  private val ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray
  private val MrnRandomPartLength = 13

  private def randomAlphaNumeric: String =
    (1 to MrnRandomPartLength).map { _ =>
      val charPos = Math.abs(Random.nextInt() % ALPHA_NUMERIC_STRING.length)
      ALPHA_NUMERIC_STRING(charPos)
    }.mkString("")

}
