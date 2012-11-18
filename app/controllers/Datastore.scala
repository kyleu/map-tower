package controllers

import play.api._
import play.api.mvc._
import controllers.forms._
import maptower.data._
import maptower.map.Point
import maptower.map.osm.{ OsmImporter, OsmConverter }

import com.codahale.jerkson.Json._
import com.mongodb.casbah.commons.{ MongoDBObject => Obj }

object Datastore extends Controller {
  def index(db: String) = Action { implicit request =>
    val dao = getDao(db)
    Ok(views.html.admin.datastore(db, dao.getInfo))
  }

  def rebuild(key: String) = Action { implicit request =>
    wipe("osm")
    wipe("map")
    wipe("game")
    loadOsm(key)
    convertOsm()
    seed()

    Redirect(controllers.routes.Admin.index()).flashing("success" -> "Rebuilt for \"%s\".".format(key));
  }

  def wipe(db: String) = Action { implicit request =>
    val dao = getDao(db)
    dao.mongoDb.dropDatabase()
    dao.wipe
    dao.ensureIndexes
    Redirect(controllers.routes.Datastore.index(db)).flashing("success" -> (db + " wiped."));
  }

  def loadOsm(key: String) = Action { implicit request =>
    val numRecords = OsmImporter("data/" + key + ".osm")
    osmDao.ensureIndexes
    Redirect(controllers.routes.Datastore.index("osm")).flashing("success" -> "%s osm records loaded from \"%s.osm\".".format(numRecords, key));
  }

  def convertOsm() = Action { implicit request =>
    OsmConverter()
    mapDao.ensureIndexes
    Redirect(controllers.routes.Datastore.index("map")).flashing("success" -> "Conversion to native game format complete.");
  }

  def seed() = Action { implicit request =>
    gameDao.seed
    gameDao.ensureIndexes
    Redirect(controllers.routes.Datastore.index("game")).flashing("success" -> "Seed data loaded.");
  }

  def query(op: String, db: String, coll: String, filter: String) = Action { implicit request =>
    val dao = getDao(db)
    val c = dao.mongoDb(coll)
    val results = op match {
      case "insert" => {
        c.insert(BaseDao.fromJson(filter))
        Iterator(("status" -> "OK"))
      }

      case "delete" => {
        c.remove(BaseDao.fromJson(filter))
        Iterator(("status" -> "OK"))
      }

      case "distinct" => {
        c.distinct(filter).iterator
      }

      case "find" => {
        c.find(BaseDao.fromJson(filter)).limit(100).toIterator
      }
    }
    Ok(views.html.admin.query(op, db, coll, filter, results))
  }
}
