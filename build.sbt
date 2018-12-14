import sbt.Keys.libraryDependencies

name := "CSYE7200-MovieRecommendation"
version := "0.1"
val akkaVersion = "2.5.18"
val akkaHttpVersion = "10.1.5"

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
    commonSettings,
    libraryDependencies ++= {
      Seq(
        "io.spray" %% "spray-json" % "1.3.5",
        "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-remote" % akkaVersion,
        "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
        "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion,
        "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
        "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
        "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,
        "com.typesafe.akka" %% "akka-stream" % akkaVersion,
        "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
        "com.github.scopt" %% "scopt" % "3.7.0"
      )
    },
    Compile/mainClass := Some("com.csye7200.model.Main"),
    fork in run := true,
  )

lazy val DataCenter = project
  .settings(
    name := "DataCenter",
    dataCenterSettings
  )

lazy val commonSettings = Seq(
  libraryDependencies ++= {
    Seq(
      "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
    )
  },
  scalaVersion := "2.12.2"
)


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
  )


)

