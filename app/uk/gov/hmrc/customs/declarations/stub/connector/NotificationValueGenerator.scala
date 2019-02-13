/*
 * Copyright 2019 HM Revenue & Customs
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

class NotificationValueGenerator {

 def generateMRN()= {
    "18GBJ" + randomAlphaNumeric(13)
  }

  private def generateRandomNumber(start: Int, end: Int): Int = {
    val rnd = new scala.util.Random
    start + rnd.nextInt( (end - start) + 1 )
  }

  private val ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"


  private def randomAlphaNumeric(count: Int): String = {
    val builder = new StringBuilder
    for(x <- 1 to count){
      val charPos = (Math.random * ALPHA_NUMERIC_STRING.length).toInt
      builder.append(ALPHA_NUMERIC_STRING.charAt(charPos))
    }
    builder.toString
  }


}
