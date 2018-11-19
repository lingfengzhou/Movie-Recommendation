package com.csye7200.model.frontend

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.csye7200.model.Config
import com.csye7200.model.frontend.routes.HttpRoute

import scala.concurrent.ExecutionContextExecutor

object ModelFrontend {
  def apply(args: Array[String]): ModelFrontend = new ModelFrontend(args)
}

class ModelFrontend(args: Array[String]) extends HttpRoute {
  val config: com.typesafe.config.Config = Config.getFrontendConfig(args)

  implicit val system: ActorSystem = ActorSystem("ModelCluster", config)
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val postSender: ActorRef = system.actorOf(Props[PostSender], name = "frontend")

  private val mode = config.getString("frontend.mode")

  private val interface: String = config.getString(s"frontend.$mode.interface")
  private val port: Int = config.getInt(s"frontend.$mode.port")

  def start {
    Http().bindAndHandle(route, interface, port)

    println(s"Server online at $interface:$port")
  }
}