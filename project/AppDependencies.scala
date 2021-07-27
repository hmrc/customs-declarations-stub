import play.sbt.PlayImport._
import sbt._

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-backend-play-28"  % "5.7.0",
    "uk.gov.hmrc" %% "simple-reactivemongo"       % "8.0.0-play-28",
    "uk.gov.hmrc" %% "wco-dec"                    % "0.35.0",

    //Pegging at 2.11.4 till this issue is resolved: https://github.com/FasterXML/jackson-module-kotlin/issues/396#issuecomment-854407157
    "com.fasterxml.jackson.module"      %% "jackson-module-scala"           % "2.11.4",
    "com.fasterxml.jackson.dataformat"  % "jackson-dataformat-xml"          % "2.11.4",
    "com.fasterxml.jackson.dataformat"  % "jackson-dataformat-properties"   % "2.11.4"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"          %% "scalatest"          % "3.2.9"   % "test",
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0"   % "test",
    "org.scalatestplus"      %% "mockito-3-4"        % "3.2.9.0" % "test",
    "com.vladsch.flexmark"   %  "flexmark-all"       % "0.36.8"  % "test"
  )
}
