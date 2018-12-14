name := "CSYE7200-MovieRecommendation"
version := "0.1"

lazy val root = project.in(file(".")).aggregate(Controller, Model, DataCenter)

lazy val Controller = project
  .enablePlugins(PlayScala)
  .settings(
    name := "Controller",
    commonSettings,
    libraryDependencies ++= Seq("com.typesafe.play" %% "play" % "2.6.20"),
    libraryDependencies += guice,
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
    libraryDependencies += ws
  )

lazy val Model = project
  .settings(
    name := "Model",
    commonSettings
  )

lazy val DataCenter = project
  .settings(
    name := "DataCenter",
    dataCenterSettings
  )

lazy val commonSettings = Seq(
  scalaVersion := "2.12.2"
)

lazy val dataCenterSettings = Seq(
  scalaVersion := "2.11.12"
)
