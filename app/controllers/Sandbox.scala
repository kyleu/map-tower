package controllers

import java.util.Date
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.{ MongoDBObject => Obj }
import map.{ Point, Bounds }
import map.osm.Node
import play.api._
import play.api.mvc._
import controllers.forms._

import com.codahale.jerkson.Json._

object Sandbox extends Controller {
  def rebuildDatastore = Action {
    models.DatabaseBuilder.wipe
    models.DatabaseBuilder.create
    Ok("OK")
  }

  private val center = new Point(-84.3856, 33.7612)

  private val mongoDb = MongoConnection()("maptower")
  private def queryNodesNear(p: Point) = Obj("loc" -> Obj("$near" -> Array(p.x, p.y)))
  private def queryNodesWithin(b: Bounds) = Obj("loc" -> Obj("$within" -> Obj("$box" -> Array(Array(b.ul.x, b.ul.y), Array(b.br.x, b.br.y)))))

  def mapTest = Action {
    Ok(views.html.maptest(center))
  }

  def pathTest() = Action { implicit request =>
    val bounds = Forms.bounds.bindFromRequest.get

    //val bounds = new Bounds(new Point(-84.39074993133545, 33.75963771197399), new Point(-84.38045024871826, 33.76275954956179))
    println(" --- " + bounds)

    val nodeObjs = mongoDb("node").find(queryNodesWithin(bounds))
    val nodes = for (nodeObj <- nodeObjs) yield Node(nodeObj)
    Ok(generate(nodes)).as("application/json")
  }
}
