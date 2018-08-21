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

package uk.gov.hmrc.customs.test

import akka.stream.Materializer
import config.AppConfig
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.Logger
import play.api.http.Status
import play.api.libs.concurrent.Execution.Implicits
import play.api.mvc.{AnyContentAsXml, AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag
import scala.util.{Success, Try}

trait CustomsPlaySpec extends PlaySpec with OneAppPerSuite  with MockitoSugar with ScalaFutures {

  implicit val mat: Materializer = app.materializer
  implicit val ec: ExecutionContext = Implicits.defaultContext
  implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  implicit val patience: PatienceConfig = PatienceConfig(timeout = 5.seconds, interval = 50.milliseconds) // be more patient than the default

  protected val contextPath: String = ""

  class RequestScenario(method: String = "POST", uri: String = s"/$contextPath/", headers: Map[String, String] = Map.empty, body:String) {

        val req : FakeRequest[AnyContentAsXml] = basicRequest(method, uri, headers).withXmlBody(scala.xml.XML.loadString(body))
 }

  class InvalidRequestScenario(method: String = "POST", uri: String = s"/$contextPath/", headers: Map[String, String] = Map.empty, body:String) {

         val req: FakeRequest[String] = basicRequest(method, uri, headers).withBody(body)
  }

  protected def component[T: ClassTag]: T = app.injector.instanceOf[T]

  protected def basicRequest(method: String = "POST", uri: String = "/", headers: Map[String, String] = Map.empty): FakeRequest[AnyContentAsEmpty.type] =
    {
      Logger.debug("request scenario method is " + method+ "Uri is " + uri)
      FakeRequest(method, uri).withHeaders(headers.toSeq: _*)
    }

  protected def requestScenario(method: String = "POST",
                                uri: String = s"/$contextPath/",
                                headers: Map[String, String] = Map.empty,
                                 body:String)(test: Future[Result] => Unit): Unit = {

    val req = Try(scala.xml.XML.loadString(body)) match {
      case Success(_) => new RequestScenario(method, uri, headers,body) {
        test(route(app, req).get)
      }
      case _ => new InvalidRequestScenario(method, uri, headers,body) {
        test(route(app, req).get)
      }
    }
  }

  protected def wasOk(resp: Future[Result]): Unit = status(resp) must be (Status.ACCEPTED)

  protected def wasNotAcceptable(resp: Future[Result]): Unit = status(resp) must be (Status.NOT_ACCEPTABLE)
  protected def wasUnsupported(resp: Future[Result]): Unit = status(resp) must be (Status.UNSUPPORTED_MEDIA_TYPE)


  protected def wasBadRequest(resp: Future[Result]): Unit = status(resp) must be (Status.BAD_REQUEST)

  protected def uriWithContextPath(path: String): String = s"$contextPath$path"


}
