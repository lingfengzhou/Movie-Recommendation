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
import akka.http.scaladsl.model.{HttpHeader, StatusCode, StatusCodes}
import akka.util.Timeout
import com.csye7200.datacenter.ClassificationActor._
import org.apache.spark.sql.DataFrame

import scala.concurrent.Future

trait ClassificationRoutes extends JsonSupport{

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[ClassificationRoutes])

  def classificationActor: ActorRef

//  implicit lazy val timeout = Timeout(5.seconds)

  lazy val classificationRoutes: Route =
    pathPrefix("movies") {
      concat(
        pathPrefix("module") {
          concat(
            post {
              path("update") {
                val classified: Future[ActionPerformed] =
                  (classificationActor .?(DoClassification)(600.seconds) ).mapTo[ActionPerformed]
                onSuccess(classified) { performed =>
                  complete(StatusCodes.Created, performed)
                }
              }
            }
          )
        }
        ,
        pathPrefix("element"){
//          concat(
//          path(Segment) { title=>
//              get {
//                val maybeMovies: Future[Option[RecMovies]] =
//                  (classificationActor ? GetRecMovies(title,limit = 3)).mapTo[Option[RecMovies]]
//                rejectEmptyResponse{
//                  complete(maybeMovies)
//                }
//              }
//          },
          post{
            path("getMovie"){
              entity(as[Info]) { info =>
                val maybeMovies: Future[String] =
                  (classificationActor .?(GetRecMovies(info.title,info.limit))(20.seconds)).mapTo[String]
                onSuccess(maybeMovies) {
                  case perform : String => complete((StatusCodes.OK),perform)
//                  performed: Option[RecMovies] => performed match {
//                  case Some(movies) => complete(StatusCodes.Found, movies)
//                  case None => complete(StatusCodes.NotFound)
                }
              }
            }
          }
        }

      )
    }

}
