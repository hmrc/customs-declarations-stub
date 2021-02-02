import play.sbt.PlayImport._
import sbt._

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-backend-play-27"  % "3.3.0",
    "uk.gov.hmrc" %% "simple-reactivemongo"       % "7.31.0-play-27",
    "uk.gov.hmrc" %% "wco-dec"                    % "0.35.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"          %% "scalatest"          % "3.2.3"   % "test",
    "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3"   % "test",
    "org.scalatestplus"      %% "mockito-3-4"        % "3.2.3.0" % "test",
    "com.vladsch.flexmark"   %  "flexmark-all"       % "0.36.8"  % "test"
  )
}
