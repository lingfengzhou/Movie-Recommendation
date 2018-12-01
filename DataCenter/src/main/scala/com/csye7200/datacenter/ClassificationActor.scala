package com.csye7200.datacenter

import akka.actor.{Actor, ActorLogging, Props}
import org.joda.time.DateTime


final case class Movie(title: String, genres: String, runningTime: Int,
                       startYear: Int, directors: String, rating: Double, numVotes: Int)
final case class Info(title: String, limit: Int)
//recommended movies
final case class RecMovies(movies: Seq[Movie])

object ClassificationActor{

  final case class ActionPerformed(description: String)
  final case class GetRecMovies(title: String, limit: Int)
  final case object DoClassification

  def props: Props = Props[ClassificationActor]

}

class ClassificationActor extends Actor with ActorLogging{

  import ClassificationActor._

  override def receive: Receive = {

    case DoClassification =>
      DoKmeans.train()
      sender() ! ActionPerformed(s"Classification completed at ${DateTime.now().toString()}!")

    case GetRecMovies(title,limit) =>
      val recMovies = GetResults.getmovies(title,limit)
      sender() ! recMovies
  }
}
