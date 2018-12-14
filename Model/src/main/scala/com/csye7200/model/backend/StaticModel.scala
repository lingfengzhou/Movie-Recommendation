package com.csye7200.model.backend

import akka.cluster.{Cluster, Member, MemberStatus}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.actor.{Actor, ActorLogging, RootActorPath}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.csye7200.model._
import com.typesafe.config.Config

import scala.concurrent.Future

class StaticModel(config: Config, dataSourceOption: Option[RequestEntity => HttpResponse]) extends Actor with ActorLogging with JsonSupport {
  def this(config: Config) = this(config, None)

  import context.dispatcher
  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])
  override def postStop(): Unit = cluster.unsubscribe(self)

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  val http = Http(context.system)

  def register(member: Member): Unit = {
    if (member.hasRole("frontend")) context.actorSelection(RootActorPath(member.address) / "user" / "frontend") ! BackendRegistration
  }

  def receive: PartialFunction[Any, Unit] = {
    case Movie(title) => handleRequest(Marshal(MovieRequest(title, 3)).to[RequestEntity])
    case HttpResponse(StatusCodes.OK, _, entity, _) =>
      log.debug(s"Receive movie info: $entity")
      handleMovieInfo(Unmarshal(entity.withContentType(ContentTypes.`application/json`)).to[MovieResponse])
    case resp@HttpResponse(code, _, _, _) =>
      log.info("Request failed, response code: " + code)
      resp.discardEntityBytes()
    case state: CurrentClusterState =>
      state.members.filter(_.status == MemberStatus.Up) foreach register
    case MemberUp(m) => register(m)
  }

  private def handleRequest(entityFuture: Future[RequestEntity]): Unit = {
    val originalSender = sender()
    for (entity <- entityFuture)
      dataSourceOption match {
        // This structure is used for test.
        case Some(dataSource) =>
          self tell (dataSource.apply(entity), originalSender)
        case None =>
          for(response <- http.singleRequest(HttpRequest(HttpMethods.POST, config.getString("backend.dataCenterUrl") + "/movies/element/getMovie", entity = entity)))
            self tell (response, originalSender)
      }
  }

  private def handleMovieInfo(movieResponseFuture: Future[MovieResponse]): Unit = {
    val originalSender = sender()
    for (movieResponse <- movieResponseFuture; origin = movieResponse.origin; related = movieResponse.related; sortedRelated = related.sortBy[Int](movieInfo => {
      if(movieInfo.director == origin.director)
        if(movieInfo.actor == origin.actor)
          2
        else
          1
      else
        0
    })) originalSender ! MovieResponse(origin, sortedRelated)
  }
}
