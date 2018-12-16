package com.csye7200.datacenter

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActors, TestKit, TestProbe}
import com.csye7200.datacenter.ClassificationActor.GetRecMovies
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.concurrent.duration._


class ActorsSpec extends TestKit(ActorSystem("ActorsSpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll: Unit = {
    shutdown(system)
  }

  "ClassificationActor" should {
    "pass on a getMovies message" in {
      within(20 seconds) {
        //      val testProbe = TestProbe()
        val msg = GetRecMovies("Jana, the Girl from the Bohemian Forest", 1)
        val testClassActor = system.actorOf(ClassificationActor.props)
        testClassActor ! msg
        expectMsg("{ \"origin\": {\"title\":\"Jana, the Girl from the Bohemian Forest\",\"genres\":[\"Drama\"],\"director\":\"Robert Land\",\"actor\":\"Leny Marenbach\",\"length\":2323.0,\"score\":4.4,\"voteNumber\":5,\"year\":1935},\"related\": [{\"title\":\"The Cure for Insomnia\",\"genres\":[\"Documentary\",\"Music\"],\"director\":\"John Henry Timmis IV\",\"actor\":\"\",\"length\":5220.0,\"score\":5.6,\"voteNumber\":339,\"year\":1987}] }")
      }
    }
    "pass on a getMovies message2" in {
      within(20 seconds) {
        //      val testProbe = TestProbe()
        val msg = GetRecMovies("The Cure for Insomnia", 1)
        val testClassActor = system.actorOf(ClassificationActor.props)
        testClassActor ! msg
        expectMsg("{ \"origin\": {\"title\":\"The Cure for Insomnia\",\"genres\":[\"Documentary\",\"Music\"],\"director\":\"John Henry Timmis IV\",\"actor\":\"\",\"length\":5220.0,\"score\":5.6,\"voteNumber\":339,\"year\":1987},\"related\": [{\"title\":\"Jana, the Girl from the Bohemian Forest\",\"genres\":[\"Drama\"],\"director\":\"Robert Land\",\"actor\":\"Leny Marenbach\",\"length\":2323.0,\"score\":4.4,\"voteNumber\":5,\"year\":1935}] }")
      }
    }
  }

}
