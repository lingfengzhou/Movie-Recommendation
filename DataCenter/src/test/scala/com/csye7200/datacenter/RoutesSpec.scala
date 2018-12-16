package com.csye7200.datacenter

import akka.actor.ActorRef
import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpHeader, HttpRequest, MessageEntity}
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.duration._
import akka.testkit.TestDuration

class RoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
  with ClassificationRoutes {
  override val classificationActor: ActorRef = system.actorOf(ClassificationActor.props, "classificationActor")

  lazy val routes = classificationRoutes
  implicit val timeout = RouteTestTimeout(20.seconds dilated)
  "GetMovieRoutes" should {
    "return ok"in {
      val info = Info("Jana, the Girl from the Bohemian Forest",3)
      val infoEntity = Marshal(info).to[MessageEntity].futureValue

      val request = Post("/movies/element/getMovie").withEntity(infoEntity)
      request ~> routes ~> check{
        status should === (StatusCodes.OK)
    }
  }
  }
}
