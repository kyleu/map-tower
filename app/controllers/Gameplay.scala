package controllers

import controllers.forms.Forms
import maptower.data.{ osmDao, mapDao }
import maptower.game.{ Room, GameTypeHelper }
import play.api.mvc.{ WebSocket, Controller, Action }
import play.api.libs.json.Json
import maptower.util.JsonWrites._

object Gameplay extends Controller {
  def index(id: String, username: String) = Action { implicit request =>
    val gameType = GameTypeHelper.types(id)
    Ok(views.html.gameplay(gameType, username))
  }

  def osm(id: String) = Action { implicit request =>
    val bounds = Forms.bounds.bindFromRequest.get

    val osmNodes = osmDao.nodesWithin(bounds) toSeq
    val osmNodeIds = osmNodes map (_.osmId) toArray
    val osmWays = osmDao.waysContainingNodes(osmNodeIds) toSeq
    val osmRelations = osmDao.relationsContainingNodes(osmNodeIds) toSeq

    val osmNodesJs = Json.toJson(osmNodes)
    val osmWaysJs = Json.toJson(osmWays)
    val osmRelationsJs = Json.toJson(osmRelations)

    val result = Map("nodes" -> osmNodesJs, "ways" -> osmWaysJs, "relations" -> osmRelationsJs)
    val rsp = Json.toJson(result).toString
    Ok(rsp).as("application/json")
  }

  def data(id: String) = Action { implicit request =>
    val bounds = Forms.bounds.bindFromRequest.get

    println(bounds)

    val nodes = mapDao.nodesWithin(bounds).toSeq
    println("1")
    val ways = mapDao.waysIntersecting(bounds, true).toSeq
    println("1")

    val nodesJs = Json.toJson(nodes)
    val waysJs = Json.toJson(ways)
    println("2")
    val result = Map("nodes" -> nodesJs, "ways" -> waysJs)
    val rsp = Json.toJson(result).toString
    println("######")
    println(rsp)
    Ok(rsp).as("application/json")
  }

  def events(id: String, username: String) = WebSocket.async[String] { request =>
    Room.join(username)
  }
}
