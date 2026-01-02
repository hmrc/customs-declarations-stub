
val appName = "customs-declarations-stub"

PlayKeys.devSettings := List("play.server.http.port" -> "6790")

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    majorVersion := 0,
    scalaVersion := "3.3.7",
    scalacOptions ++= scalacFlags,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test
  )
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427

lazy val scalacFlags = Seq(
  "-deprecation",            // warn about use of deprecated APIs
  "-encoding", "UTF-8",      // source files are in UTF-8
  "-feature",                // warn about misused language features
  "-unchecked",              // warn about unchecked type parameters
  "-Xfatal-warnings",        // warnings are fatal!!
  "-Wconf:src=routes/.*&msg=unused import:silent", // silent "unused import" warnings from Play routes
  "-Wconf:src=routes/.*&msg=unused private member:silent",
  "-Wconf:src=routes/.*&msg=unused pattern variable:silent",
  "-Wconf:src=app/repositories/.*&msg=unused explicit parameter:silent",
  "-Wconf:msg=Flag.*repeatedly:s"
)
