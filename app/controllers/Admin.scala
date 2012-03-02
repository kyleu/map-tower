package controllers

import play.api._
import play.api.mvc._

object Admin extends Controller {
  def index = Action { implicit request =>
    Ok(views.html.admin.index())
  }

  def logs = Action {
    TODO
  }
}
