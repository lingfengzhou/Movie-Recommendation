package com.csye7200.datacenter

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import org.apache.spark.sql.SparkSession

import scala.concurrent.Await
import scala.concurrent.duration.Duration


object MovieServer extends App with ClassificationRoutes {
  // set up ActorSystem and other dependencies here
  //#main-class
  //#server-bootstrapping
  implicit val system: ActorSystem = ActorSystem("MovieRecommendationServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  //#server-bootstrapping

  val classificationActor: ActorRef = system.actorOf(ClassificationActor.props, "classificationActor")
  val spark = SparkSession.builder
    .master(Config.getSparkMaster())
    .appName(Config.getSparkName())
    .getOrCreate()
  spark.sparkContext.setLogLevel(Config.getLogLevel())
  //#main-class
  // from the UserRoutes trait
  lazy val routes: Route = classificationRoutes
  //#main-class

  //#http-server
  Http().bindAndHandle(routes, Config.getSeverInterface(), Config.getSeverPort())

  println(s"Server online at http://${Config.getSeverInterface()}:${Config.getSeverPort()}")

  Await.result(system.whenTerminated, Duration.Inf)
  //#http-server
  //#main-class
}
//#main-class
//#quick-start-server

