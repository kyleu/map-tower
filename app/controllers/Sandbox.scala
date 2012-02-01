package controllers

import play.api._
import play.api.mvc._

object Sandbox extends Controller {
  def index = Action {
    models.DatabaseBuilder.wipe
    models.DatabaseBuilder.create
    Ok("OK")
  }
}