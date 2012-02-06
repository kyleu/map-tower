package controllers

import scala.io.Source
import java.util.Date
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.gridfs.Imports._
import map.{ Point, Bounds, Line }
import play.api._
import play.api.mvc._

import com.codahale.jerkson.Json._

object Tiles extends Controller {
  private val mongoDb = MongoConnection()("maptower")
  private val mongoFs = GridFS(MongoConnection()("tiles"))

  private val styleId = 998

  def get(z: Int, x: Int, y: Int) = Action {
    val url = "http://%s.tile.cloudmade.com/0320d0049e1a4242bab7857cec8b343a/%s/256/%s/%s/%s.png".format("a", styleId, z, x, y)

    val source = Source.fromURL(url)

    val dir = play.Play.application().getFile("data/cache")

    Ok("")
  }
}

object tileCache
