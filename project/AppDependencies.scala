import play.sbt.PlayImport._
import sbt._

object AppDependencies {

  val jacksonVersion = "2.9.6"

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-25" % "3.8.0",
    "uk.gov.hmrc" %% "auth-client" % "2.11.0-play-25",
    "uk.gov.hmrc" %% "play-reactivemongo" % "6.2.0",
    "uk.gov.hmrc" %% "wco-dec" % "0.3.0"
  )

  def test(scope: String = "test") = Seq(
    "org.scalatest" %% "scalatest" % "3.0.4" % scope,
    "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1" % "test",
    "org.pegdown" % "pegdown" % "1.6.0" % scope
  )

}
