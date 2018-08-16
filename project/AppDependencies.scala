import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val jacksonVersion = "2.9.6"

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-25" % "1.7.0"

  )

  def test(scope: String = "test") = Seq(
    "org.scalatest" %% "scalatest" % "3.0.4" % scope,
    "org.pegdown" % "pegdown" % "1.6.0" % scope,
    "org.jsoup" % "jsoup" % "1.10.2" % scope,
    "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
    "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1" % scope,
    "org.mockito" % "mockito-core" % "2.13.0" % scope,
    "com.github.tomakehurst" % "wiremock" % "2.6.0" % scope
  )

}
