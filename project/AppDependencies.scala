import play.sbt.PlayImport._
import sbt._

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-backend-play-26"  % "3.3.0",
    "uk.gov.hmrc" %% "simple-reactivemongo"       % "7.31.0-play-26",
    "uk.gov.hmrc" %% "wco-dec"                    % "0.35.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"           %%  "scalatest"           % "3.0.9"   % "test",
    "org.scalatestplus.play"  %%  "scalatestplus-play"  % "3.1.2"   % "test",
    "org.pegdown"             %   "pegdown"             % "1.6.0"   % "test",
    "org.mockito"             %   "mockito-core"        % "3.5.7"   % "test"
  )

}
