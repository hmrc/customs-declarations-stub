import play.sbt.PlayImport._
import sbt._

object AppDependencies {

  val bootstrapPlayVersion = "7.13.0"
  val jacksonVersion = "2.14.2"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"       %% "bootstrap-backend-play-28" % bootstrapPlayVersion,
    "uk.gov.hmrc"       %% "wco-dec"                   % "0.37.0",

    "com.fasterxml.jackson.module"      %% "jackson-module-scala"          % jacksonVersion,
    "com.fasterxml.jackson.dataformat"  %  "jackson-dataformat-xml"        % jacksonVersion,
    "com.fasterxml.jackson.dataformat"  %  "jackson-dataformat-properties" % jacksonVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"  % bootstrapPlayVersion % "test",
    "org.mockito"            %% "mockito-scala"           % "1.17.12"            % "test",
  )
}
