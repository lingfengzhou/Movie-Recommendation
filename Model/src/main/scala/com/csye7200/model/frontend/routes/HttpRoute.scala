package com.csye7200.model.frontend.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.csye7200.model.{JobFailed, JsonSupport, Movie, MovieInfo}
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


trait HttpRoute extends Directives with JsonSupport {

  implicit val system: ActorSystem
  implicit val executor: ExecutionContext
  implicit val materializer: ActorMaterializer
  implicit val timeout: Timeout = 3.seconds

  val config: Config

  val postSender: ActorRef

  lazy val route: Route =
    path("query") {
      post {
        entity(as[Movie]) {
          movie => {
            onSuccess(postSender ? movie) {
              case result: Seq[MovieInfo] => complete(result)
              case JobFailed(reason) => complete(reason)
            }
          }
        }
      }
    }

}
