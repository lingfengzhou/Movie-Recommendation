name := "CSYE7200-MovieRecommendation"
version := "0.1"

lazy val root = project.in(file(".")).aggregate(Controller, Model, DataCenter)

lazy val Controller = project
  .settings(
    name := "Controller",
    commonSettings
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