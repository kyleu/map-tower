package controllers

import play.api._
import play.api.mvc._
import controllers.forms._
import maptower.data._
import maptower.map.Point

import com.codahale.jerkson.Json._

object Game extends Controller {
  private val center = Point(-84.3856, 33.7612)

  def index(id: String) = Action {
    Ok(views.html.game(center))
  }

  def osm(id: String) = Action { implicit request =>
    val bounds = Forms.bounds.bindFromRequest.get

    val osmNodes = osmDao.nodesWithin(bounds) toSeq
    val osmNodeIds = osmNodes map (_.osmId) toArray
    val osmWays = osmDao.waysContainingNodes(osmNodeIds) toSeq
    val osmRelations = osmDao.relationsContainingNodes(osmNodeIds) toSeq
    val rsp = generate(Map("nodes" -> osmNodes, "ways" -> osmWays, "relations" -> osmRelations))
    Ok(rsp).as("application/json")
  }

  def data(id: String) = Action { implicit request =>
    val bounds = Forms.bounds.bindFromRequest.get

    val nodes = mapDao.nodesWithin(bounds) toSeq
    val ways = mapDao.waysIntersecting(bounds, true) toSeq
    val rsp = generate(Map("nodes" -> nodes, "ways" -> ways))
    Ok(rsp).as("application/json")
  }
}
