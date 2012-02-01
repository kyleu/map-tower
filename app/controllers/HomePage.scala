package controllers

import play.api._
import play.api.mvc._

object HomePage extends Controller {
  def index = Action {
    Ok(views.html.index("Good to see you. Hope you're having a good day."))
  }
}