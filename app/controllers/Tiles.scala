package controllers

import play.api._
import play.api.mvc._
import play.api.libs.iteratee.Enumerator
import maptower.data.MongoClient

object Tiles extends Controller with MongoClient {
  val dbName = "maptower"
  def get(z: Int, x: Int, y: Int) = Action {
    val file = tileDao.get(z, y, x)
    Ok.stream(Enumerator.fromStream(file.inputStream)).as("image/png").withHeaders(CACHE_CONTROL -> "max-age=3600", EXPIRES -> "Tue, 14 Feb 2012 14:19:41 GMT")
  }
}