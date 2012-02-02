package controllers

import java.util.Date
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.{ MongoDBObject => Obj }
import map.{ Point, Bounds }
import play.api._
import play.api.mvc._

import com.codahale.jerkson.Json._

object Sandbox extends Controller {
  def rebuildDatastore = Action {
    models.DatabaseBuilder.wipe
    models.DatabaseBuilder.create
    Ok("OK")
  }

  private val center = new Point(-84.3856, 33.7612, Map("a" -> "x", "b" -> "y"))
  var bounds = new Bounds(new Point(-84.39074993133545, 33.75963771197399), new Point(-84.38045024871826, 33.76275954956179))

  private val mongoDb = MongoConnection()("maptower")
  private def queryNodesNear(p: Point) = Obj("loc" -> Obj("$near" -> Array(p.lng, p.lat)))
  private def queryNodesWithin(b: Bounds) = Obj("loc" -> Obj("$within" -> Obj("$box" -> Array(Array(b.ul.lng, b.ul.lat), Array(b.br.lng, b.br.lat)))))

  def mapTest = Action {
    val nodes = mongoDb("node").find(queryNodesWithin(bounds))
    val points = for (node <- nodes) yield Point.apply(node)
    Ok(views.html.maptest(center, points))
  }

  def pathTest = Action {
    val nodes = mongoDb("node").find(queryNodesWithin(bounds))
    val points = for (node <- nodes) yield Point.apply(node)
    Ok(generate(points)).as("application/json")
  }
}