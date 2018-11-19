package com.csye7200.model.backend

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.csye7200.model.Config

import scala.concurrent.ExecutionContextExecutor

object ModelBackend {
  def apply(args: Array[String]): ModelBackend = new ModelBackend(args)
}

class ModelBackend(args: Array[String]) {
  val config: com.typesafe.config.Config = Config.getBackendConfig(args)

  implicit val system: ActorSystem = ActorSystem("ModelCluster", config)
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def start: Unit = {
    system.actorOf(Props(classOf[StaticModel], config), name = "backend")
  }
}
