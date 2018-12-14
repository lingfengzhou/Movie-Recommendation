package controllers

import javax.inject.Inject
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SearchController @Inject()(cc: MessagesControllerComponents)
                                (ws: WSClient)
                                (config: Configuration) extends MessagesAbstractController(cc) {

  def search(title: String): Action[AnyContent] = Action.async {
    println(title)
    val data = Json.obj(
      "title" -> title,
    )
    config.getOptional[String]("backend.url") match {
      case Some(url) => ws.url(url + "/search").post(data).map {
        response => Ok(response.body)
      }
      case None => Future(InternalServerError(""))
    }
  }
}
