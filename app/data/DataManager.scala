package data;

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.casbah.Imports._
import map._
import map.osm._

object DataManager {
  def wipe(db: String) {
    val mongoDb = getConn(db)
    mongoDb.dropDatabase()
  }

  def index(db: String) {
    val mongoDb = getConn(db)
    mongoDb("node").createIndex(Obj("osmid" -> 1))
    mongoDb("node").createIndex(Obj("tags.k" -> 1))
    mongoDb("node").createIndex(Obj("loc" -> "2d"))

    mongoDb("way").createIndex(Obj("osmid" -> 1))
    mongoDb("way").createIndex(Obj("tags.k" -> 1))
    mongoDb("way").createIndex(Obj("nodes" -> 1))

    mongoDb("relation").createIndex(Obj("osmid" -> 1))
    mongoDb("relation").createIndex(Obj("tags.k" -> 1))
    mongoDb("relation").createIndex(Obj("members.type" -> 1))
    mongoDb("relation").createIndex(Obj("members.ref" -> 1))
  }

  def normalize(db: String) {
    val mongoDb = getConn(db)

    val osmNodeColl = mongoDb("node").find(Obj("tags.k" -> "name")) map (OsmNode(_))
    for (osmNode <- osmNodeColl) println(convert(osmNode))

    val wayColl = mongoDb("way")

    val relationColl = mongoDb("relation")
  }

  private def convert(osmNode: OsmNode) = {
    var name = osmNode.tags("name")
    new Node(osmNode.osmId, name, osmNode.loc, osmNode.tags)
  }

  def getConn(db: String) = {
    val mongoConn = MongoConnection()
    mongoConn(db)
  }
}