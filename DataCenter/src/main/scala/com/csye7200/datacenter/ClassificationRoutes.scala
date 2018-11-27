package com.csye7200.datacenter

import akka.actor.{ActorRef, ActorSystem}

import scala.concurrent.duration._
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.event.Logging
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.util.Timeout
import com.csye7200.datacenter.ClassificationActor._

import scala.concurrent.Future

trait ClassificationRoutes extends JsonSupport{

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[ClassificationRoutes])

  def classificationActor: ActorRef

  implicit lazy val timeout = Timeout(5.seconds)

  lazy val classificationRoutes: Route =
    pathPrefix("movies") {
      concat(
        pathEnd{
          concat(
            post{path("update-classification")
              {
                val classified: Future[ActionPerformed] =
                  (classificationActor ? DoKmeans).mapTo[ActionPerformed]
                onSuccess(classified){ performed =>
                  complete(StatusCodes.Created,performed)
                }
              }
            }
          )
        },
        path(Segment) { title=>
          concat(
            get {
              val maybeMovies: Future[Option[RecMovies]] =
                (classificationActor ? GetRecMovies(title,limit = 3)).mapTo[Option[RecMovies]]
              rejectEmptyResponse{
                complete(maybeMovies)
              }
            }

          )

        }
      )
    }

}
