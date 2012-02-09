package controllers

import maptower.map._
import maptower.map.osm._
import play.api._
import play.api.mvc._
import controllers.forms._
import maptower.data.MongoClient

import com.codahale.jerkson.Json._

object Sandbox extends Controller with MongoClient {
  val dbName = "maptower"

  def rebuildMaps = Action {
    mapDao.wipe
    mapDao.loadOsm(osmDao)
    mapDao.index
    Ok("OK")
  }

  def rebuildOsm = Action {
    osmDao.wipe
    OsmImporter(osmDao, "data/atlanta.osm")
    osmDao.index
    Ok("OK")
  }

  private val center = Point(-84.3856, 33.7612)

  def mapTest = Action {
    Ok(views.html.maptest(center))
  }

  def osmDataTest() = Action { implicit request =>
    val bounds = Forms.bounds.bindFromRequest.get

    val osmNodes = osmDao.nodesWithin(bounds) toSeq
    val osmNodeIds = osmNodes map (_.osmId) toArray
    val osmWays = osmDao.waysContainingNodes(osmNodeIds) toSeq
    val osmRelations = osmDao.relationsContainingNodes(osmNodeIds) toSeq
    val rsp = generate(Map("nodes" -> osmNodes, "ways" -> osmWays, "relations" -> osmRelations))
    Ok(rsp).as("application/json")
  }

  def dataTest() = Action { implicit request =>
    val bounds = Forms.bounds.bindFromRequest.get

    val nodes = mapDao.nodesWithin(bounds) toSeq
    val ways = mapDao.waysIntersecting(bounds) toSeq
    val rsp = generate(Map("nodes" -> nodes, "ways" -> ways))
    Ok(rsp).as("application/json")
  }
}
