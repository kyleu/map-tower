package controllers

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import map.{ Point, Bounds }
import map.osm._
import data.{ DataManager, OsmImporter }
import play.api._
import play.api.mvc._
import controllers.forms._

import com.codahale.jerkson.Json._

object Sandbox extends Controller {
  val dbName = "maptower"

  def rebuildDatastore = Action {
    // models.DatabaseBuilder.wipe
    // models.DatabaseBuilder.create
    DataManager.wipe(dbName)
    OsmImporter(dbName, "data/atlanta.osm")
    DataManager.index(dbName)

    Ok("OK")
  }

  private val center = new Point(-84.3856, 33.7612)

  private val mongoDb = MongoConnection()(dbName)
  private def queryNodesNear(p: Point) = Obj("loc" -> Obj("$near" -> Array(p.x, p.y)))
  private def queryNodesWithin(b: Bounds) = Obj("loc" -> Obj("$within" -> Obj("$box" -> Array(Array(b.ul.x, b.ul.y), Array(b.br.x, b.br.y)))))
  private def queryWaysByNodeOsmIds(nodeOsmIds: Array[Int]) = "nodes" $in nodeOsmIds
  private def queryRelationsByNodeOsmIds(nodeOsmIds: Array[Int]) = Obj("members.type" -> "node", "members.ref" -> nodeOsmIds)
  private def queryRelationsByWayOsmIds(nodeOsmIds: Array[Int]) = Obj("members.type" -> "way", "members.ref" -> Obj("$in" -> ObjList(nodeOsmIds)))

  def mapTest = Action {
    Ok(views.html.maptest(center))
  }

  def pathTest() = Action { implicit request =>
    val bounds = Forms.bounds.bindFromRequest.get
    val nodes = mongoDb("node") find (queryNodesWithin(bounds)) map (OsmNode(_)) toSeq
    val nodeIds = nodes map (_.osmId) toArray
    val ways = mongoDb("way") find (queryWaysByNodeOsmIds(nodeIds)) map (OsmWay(_)) toSeq
    val relations = mongoDb("relation").find(queryRelationsByNodeOsmIds(nodeIds)) map (OsmRelation(_)) toSeq
    val rsp = generate(Map("nodes" -> nodes, "ways" -> ways, "relations" -> relations))
    Ok(rsp).as("application/json")
  }
}
