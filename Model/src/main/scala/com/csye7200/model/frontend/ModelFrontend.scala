package com.csye7200.model.frontend

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.csye7200.model.Config
import com.csye7200.model.frontend.routes.HttpRoute

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object ModelFrontend {
  def apply(args: Array[String]): ModelFrontend = new ModelFrontend(args)
}

class ModelFrontend(args: Array[String]) extends HttpRoute {
  val config: com.typesafe.config.Config = Config.getFrontendConfig(args)

  implicit val system: ActorSystem = ActorSystem("ModelCluster", config)
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val logger = Logging(system, getClass)

  val postSender: ActorRef = system.actorOf(Props[PostSender], name = "frontend")

  private val mode = config.getString("frontend.mode")

  private val interface: String = config.getString(s"frontend.$mode.interface")
  private val port: Int = config.getInt(s"frontend.$mode.port")

  def start {
    start(port)
  }

  private def start(port: Int): Unit = {
    Http().bindAndHandle(route, interface, port) onComplete {
      case Success(_) => logger.info(s"Server listens on $interface:$port")
      case Failure(exception) => exception.getCause match {
        case _: java.net.BindException => start(port + 1)
        case causeException => causeException.printStackTrace()
      }
    }
  }
}