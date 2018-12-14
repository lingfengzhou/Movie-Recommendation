package controllers

import javax.inject.Inject
import play.api.mvc._

class IndexController @Inject()(cc: MessagesControllerComponents) extends MessagesAbstractController(cc) {

  def index: Action[AnyContent] = Action {
    Ok(views.html.index())
  }
}
