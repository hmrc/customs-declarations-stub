import play.sbt.PlayImport.ws
import sbt.*

object AppDependencies {

  val bootstrapPlayVersion = "9.19.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"  %% "bootstrap-backend-play-30"  % bootstrapPlayVersion,
    "uk.gov.hmrc"  %% "wco-dec"                    % "0.43.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"  %% "bootstrap-test-play-30"  % bootstrapPlayVersion % "test",
    "org.scalatestplus"      %% "mockito-4-11"                       % "3.2.18.0",
  )
}
