package controllers

import play.api._
import play.api.mvc._
import controllers.forms._
import maptower.data._
import maptower.map.Point
import maptower.map.osm.OsmImporter

import com.codahale.jerkson.Json._
import com.mongodb.casbah.commons.{ MongoDBObject => Obj }

object Admin extends Controller {
  def index = Action {
    Ok(views.html.admin.index())
  }

  def datastore(db: String) = Action {
    val dao = getDao(db)
    Ok(views.html.admin.datastore(db, dao.getInfo))
  }

  def wipe(db: String) = Action {
    val dao = getDao(db)
    dao.wipe
    dao.ensureIndexes
    Ok("OK")
  }

  def loadOsm(key: String) = Action {
    OsmImporter.load(osmDao, "data/" + key + ".osm")
    osmDao.ensureIndexes
    Ok("OK")
  }

  def convertOsm() = Action {
    OsmImporter.convert(osmDao, mapDao)
    mapDao.ensureIndexes
    Ok("OK")
  }

  def logs = Action {
    TODO
  }

  def find(db: String = "map", coll: String = "node", filter: Option[String] = None, op: Option[String] = None) = Action {
    val dao = getDao(db)
    val c = dao.mongoDb(coll)
    val results = op match {
      case Some("insert") => {
        c.insert(BaseDao.fromJson(filter.getOrElse("{}")))
        Iterator(Obj("status" -> "OK"))
      }

      case Some("delete") => {
        c.remove(BaseDao.fromJson(filter.getOrElse("{}")))
        Iterator(Obj("status" -> "OK"))
      }

      case _ => c.find(BaseDao.fromJson(filter.getOrElse("{}"))).limit(100).toIterator
    }
    Ok(views.html.admin.find(db, coll, filter.getOrElse("{}"), results))
  }
}
