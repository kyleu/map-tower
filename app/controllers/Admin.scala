package controllers

import play.api._
import play.api.mvc._
import controllers.forms._
import maptower.data._
import maptower.map.Point

import com.codahale.jerkson.Json._

object Admin extends Controller {
  def index = Action {
    Ok(views.html.admin.index())
  }

  def rebuild(dbType: String) = Action {
    dbType match {
      case "osm" =>
        osmDao.wipe
        OsmImporter.load(osmDao, "data/atlanta.osm")
        osmDao.index
      case "map" =>
        mapDao.wipe
        OsmImporter.convert(osmDao, mapDao)
        mapDao.index
      case "game" =>
        gameDao.wipe
        gameDao.importJson("gametypes.json")
        gameDao.index
      case "tile" =>
        tileDao.wipe
        tileDao.index
    }
    Ok("OK")
  }

  def logs = Action {
    Ok
  }
}
