package com.csye7200.datacenter

import akka.actor.{Actor, ActorLogging, Props}
import org.joda.time.DateTime


final case class Movie(title: String, gneres: Seq[String], runningTime: Int,
                       release: Int, directors: Seq[String], rating: Double, numVotes: Int)
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
  var recMovies = Set.empty[Movie]

  override def receive: Receive = {

    case DoClassification =>
      DoKmeans
      sender() ! ActionPerformed(s"Classification completed at ${DateTime.now().toString()}!")
    case GetRecMovies(title,limit) =>
      // TODO find(title: String): RecMovies
      sender() ! RecMovies(recMovies.toSeq)
  }
}
