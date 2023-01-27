import play.sbt.PlayImport._
import sbt._

object AppDependencies {

  val bootstrapPlayVersion = "7.3.0"
  val jacksonVersion = "2.13.5"
  val mongoVersion = "0.70.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"       %% "bootstrap-backend-play-28" % bootstrapPlayVersion,
    "uk.gov.hmrc"       %% "wco-dec"                   % "0.37.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"        % mongoVersion,

    "com.fasterxml.jackson.module"      %% "jackson-module-scala"         % jacksonVersion,
    "com.fasterxml.jackson.dataformat"  % "jackson-dataformat-xml"        % jacksonVersion,
    "com.fasterxml.jackson.dataformat"  % "jackson-dataformat-properties" % jacksonVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"  % bootstrapPlayVersion,
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-28" % mongoVersion,
    "org.scalatest"          %% "scalatest"               % "3.2.13"   % "test",
    "org.scalatestplus"      %% "mockito-3-4"             % "3.2.10.0" % "test",
    "com.vladsch.flexmark"   %  "flexmark-all"            % "0.62.2"   % "test"
  )
}
