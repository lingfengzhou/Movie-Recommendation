package com.csye7200.datacenter

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

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

  //#main-class
  // from the UserRoutes trait
  lazy val routes: Route = classificationRoutes
  //#main-class

  //#http-server
  Http().bindAndHandle(routes, "0.0.0.0", 8080)

  println(s"Server online at http://0.0.0.0:8080/")

  Await.result(system.whenTerminated, Duration.Inf)
  //#http-server
  //#main-class
}
//#main-class
//#quick-start-server

