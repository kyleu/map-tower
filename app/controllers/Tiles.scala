package controllers

import play.api._
import play.api.mvc._
import play.api.libs.iteratee.Enumerator
import maptower.data._

object Tiles extends Controller {
  def get(z: Int, x: Int, y: Int) = Action {
    Logger.info("Serving tile for %s:%s/%s".format(x, y, z))
    val file = tileDao.get(z, y, x)
    Ok.stream(Enumerator.fromStream(file.inputStream)).as("image/png").withHeaders(CACHE_CONTROL -> "max-age=3600", EXPIRES -> "Tue, 14 Feb 2013 12:00:00 GMT")
  }
}