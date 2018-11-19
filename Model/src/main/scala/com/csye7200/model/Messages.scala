package com.csye7200.model

import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

final case class Movie(title: String)
final case class MovieInfo(title: String, genres: Seq[String], director: String, actor: String, length: Int,
                           score: Double, voteNumber: Int, year: Int)
final case class MovieRequest(title: String, limit: Int, threshold: Double)
final case class MovieResponse(origin: MovieInfo, related: Seq[MovieInfo], limit: Int)
final case class JobFailed(reason: String)
case object BackendRegistration

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemFormat: RootJsonFormat[MovieInfo] = jsonFormat8(MovieInfo)
  implicit val orderFormat: RootJsonFormat[Movie] = jsonFormat1(Movie)
  implicit val movieRequestFormat: RootJsonFormat[MovieRequest] = jsonFormat3(MovieRequest)
  implicit val movieResponseFormat: RootJsonFormat[MovieResponse] = jsonFormat3(MovieResponse)
}
