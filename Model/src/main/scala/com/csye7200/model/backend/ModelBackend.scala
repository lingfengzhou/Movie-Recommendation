package com.csye7200.model.backend

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.csye7200.model.Config

import scala.concurrent.ExecutionContextExecutor

object ModelBackend {
  def apply(port: Int): ModelBackend = new ModelBackend(port)
}

class ModelBackend(port: Int) {
  val config: com.typesafe.config.Config = Config.getBackendConfig(port)

  implicit val system: ActorSystem = ActorSystem("ModelCluster", config)
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def start: Unit = {
    system.actorOf(Props(classOf[StaticModel], config), name = "backend")
  }
}
