import play.sbt.PlayImport.ws
import sbt.*

object AppDependencies {

  val bootstrapPlayVersion = "9.19.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"  %% "bootstrap-backend-play-30"  % bootstrapPlayVersion,
    "uk.gov.hmrc"  %% "wco-dec"                    % "0.41.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"  %% "bootstrap-test-play-30"  % bootstrapPlayVersion % "test",
    "org.mockito"  %% "mockito-scala"           % "1.17.37"            % "test",
  )
}
