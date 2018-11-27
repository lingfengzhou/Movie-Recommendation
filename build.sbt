import sbt.Keys.libraryDependencies

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

lazy val akkaVersion    = "2.5.18"
lazy val akkaHttpVersion = "10.1.5"
lazy val dataCenterSettings = Seq(
  scalaVersion := "2.11.12",

  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-sql" % "2.4.0",
    "org.apache.spark" %% "spark-core" % "2.4.0",
    "org.apache.spark" %% "spark-mllib" % "2.4.0",

    "com.typesafe.akka" %% "akka-http"   % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream"    % akkaVersion,

    "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
    "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
    "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test

  )


)

