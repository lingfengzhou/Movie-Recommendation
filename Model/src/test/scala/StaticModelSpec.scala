import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model._
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import com.csye7200.model.{Movie, MovieInfo, MovieResponse}
import com.csye7200.model.backend.StaticModel
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.io.Source
import scala.util.Success

class StaticModelSpec() extends TestKit(ActorSystem("StaticModelSpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  def GetResult(path: String): MovieResponse = {
    val getDataSource: String => RequestEntity => HttpResponse =
      entity => { _ => HttpResponse(entity = entity, headers = List(headers.`Content-Type`(ContentTypes.`application/json`))) }
    val fileStream = getClass.getResourceAsStream(path)
    val entity = Source.fromInputStream(fileStream).mkString
    val staticModel = TestActorRef(Props(classOf[StaticModel], ConfigFactory.empty(), Some(getDataSource(entity))))
    implicit val timeout: Timeout = 3.seconds
    val future = staticModel ? Movie("asdfasd")
    val Success(result) = future.mapTo[MovieResponse].value.get
    result
  }

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "StaticModel actor" must {

    "work with response1.json" in {
      val result = GetResult("response1.json")
      result shouldBe MovieResponse(MovieInfo("Test", List(), "", "", 100, 8.9, 1000, 1980), List())
    }

    "work with response2.json" in {
      val result: MovieResponse = GetResult("response2.json")
      result.related.length shouldBe 6
    }

  }
}
