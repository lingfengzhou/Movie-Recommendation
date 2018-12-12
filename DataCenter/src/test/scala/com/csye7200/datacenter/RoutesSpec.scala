package com.csye7200.datacenter

import akka.actor.ActorRef
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures

class RoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
  with ClassificationRoutes {
  override val classificationActor: ActorRef = system.actorOf(ClassificationActor.props, "classificationActor")

  lazy val routes = classificationRoutes

  //todo "be able to update (POST /movies/update-classification)"

  //todo get movie result(title)
}
