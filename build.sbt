import sbt._
import uk.gov.hmrc.DefaultBuildSettings._

val appName = "customs-declarations-stub"

PlayKeys.devSettings := Seq("play.server.http.port" -> "6790")

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtDistributablesPlugin)
  .settings(commonSettings: _*)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    IntegrationTest / Keys.fork                   := false,
    IntegrationTest / unmanagedSourceDirectories  := (IntegrationTest / baseDirectory) (base => Seq(base / "it")).value,
    IntegrationTest / testGrouping                := oneForkedJvmPerTest((IntegrationTest / definedTests).value),
    IntegrationTest / parallelExecution           := false,
    addTestReportOption(IntegrationTest, "int-test-reports")
  )
  // .settings(scoverageSettings)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427

lazy val commonSettings = Seq(
  majorVersion := 0,
  scalaVersion := "2.13.8",
  scalacOptions ++= scalacFlags,
  libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test
)

lazy val scalacFlags = Seq(
  "-deprecation",            // warn about use of deprecated APIs
  "-encoding", "UTF-8",      // source files are in UTF-8
  "-feature",                // warn about misused language features
  "-unchecked",              // warn about unchecked type parameters
  "-Ywarn-numeric-widen",
  "-Xfatal-warnings",        // warnings are fatal!!
  "-Wconf:cat=unused-imports&src=routes/.*:s",       // silent "unused import" warnings from Play routes
  "-Wconf:site=Module.*&cat=lint-byname-implicit:s"  // silent warnings from Pureconfig/Shapeless
)

/*
lazy val scoverageSettings: Seq[Setting[_]] = Seq(
  coverageExcludedPackages := List(
    "<empty>",
    "Reverse.*",
    "domain\\..*",
    "models\\..*",
    "metrics\\..*",
    ".*(BuildInfo|Routes|Options).*"
  ).mkString(";"),
  coverageMinimumStmtTotal := 57,
  coverageFailOnMinimum := true,
  coverageHighlighting := true,
  Test / parallelExecution := false
)
*/
