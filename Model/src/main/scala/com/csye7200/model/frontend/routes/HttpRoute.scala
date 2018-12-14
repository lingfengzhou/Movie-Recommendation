package com.csye7200.model.frontend.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.csye7200.model._
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


trait HttpRoute extends Directives with JsonSupport {

  implicit val system: ActorSystem
  implicit val executor: ExecutionContext
  implicit val materializer: ActorMaterializer
  implicit val timeout: Timeout = 10.seconds

  val config: Config

  val postSender: ActorRef

  lazy val route: Route =
    path("search") {
      post {
        entity(as[Movie]) {
          movie => {
            onSuccess(postSender ? movie) {
              case movieResponse: MovieResponse => complete(movieResponse)
              case JobFailed(reason) => complete(reason)
            }
          }
        }
      }
    }
}