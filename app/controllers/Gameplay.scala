package controllers

import com.codahale.jerkson.Json.generate

import play.api._
import play.api.mvc._

import play.api.libs.json._
import play.api.libs.iteratee._

import controllers.forms.Forms
import maptower.data.{ osmDao, mapDao }
import maptower.game.{ Room, GameType }
import play.api.libs.json.JsValue
import play.api.mvc.{ WebSocket, Controller, Action }

import akka.actor._
import akka.util.duration._

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

  def events(id: String, username: String) = WebSocket.async[JsValue] { request =>
    Room.join(username)
  }
}
