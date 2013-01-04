package controllers

import controllers.forms.Forms
import maptower.data.{ osmDao, mapDao }
import maptower.game.{ Room, GameType }
import play.api.mvc.{ WebSocket, Controller, Action }

object Gameplay extends Controller {
  def index(id: String, username: String) = Action { implicit request =>
    val gameType = GameType.types(id)
    Ok(views.html.gameplay(gameType, username))
  }

  def osm(id: String) = Action { implicit request =>
    val bounds = Forms.bounds.bindFromRequest.get

    val osmNodes = osmDao.nodesWithin(bounds) toSeq
    val osmNodeIds = osmNodes map (_.osmId) toArray
    val osmWays = osmDao.waysContainingNodes(osmNodeIds) toSeq
    val osmRelations = osmDao.relationsContainingNodes(osmNodeIds) toSeq
    val result = Map("nodes" -> osmNodes, "ways" -> osmWays, "relations" -> osmRelations)
    val rsp = "" //TODO generate(result)
    Ok(rsp).as("application/json")
  }

  def data(id: String) = Action { implicit request =>
    val bounds = Forms.bounds.bindFromRequest.get

    println(bounds)

    val nodes = mapDao.nodesWithin(bounds) toSeq
    val ways = mapDao.waysIntersecting(bounds, true) toSeq
    val result = Map("nodes" -> nodes, "ways" -> ways)
    val rsp = "" //TODO generate(result)
    Ok(rsp).as("application/json")
  }

  def events(id: String, username: String) = WebSocket.async[String] { request =>
    Room.join(username)
  }
}
