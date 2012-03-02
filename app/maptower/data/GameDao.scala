package maptower.data

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.casbah.Imports._
import maptower.game._

class GameDao(dbName: String, enableTrace: Boolean) extends BaseDao(dbName, enableTrace) {
  def seed {
  }

  def ensureIndexes {
  }

  val nodeTypeColl = Array(
    new NodeType("unknown", 16, "#00f"),
    new NodeType("postcodearea", 14, "#f00"),
    new NodeType("water", 13, "#f00"),
    new NodeType("school", 17, "#f00"),
    new NodeType("university", 17, "#f00"),
    new NodeType("nodeTypesege", 17, "#f00"),
    new NodeType("cinema", 17, "#f00"),
    new NodeType("theatre", 17, "#f00"),
    new NodeType("hotel", 17, "#f00"),
    new NodeType("parking", 17, "#f00"),
    new NodeType("supermarket", 17, "#f00"),
    new NodeType("hospital", 17, "#f00"),
    new NodeType("doctors", 17, "#f00"),
    new NodeType("pharmacy", 17, "#f00"),
    new NodeType("requestedlocation", 12, "#f00"),
    new NodeType("country", 5, "#f00"),
    new NodeType("pub", 17, "#f00"),
    new NodeType("airport", 14, "#f00"),
    new NodeType("city", 10, "#f00"),
    new NodeType("town", 11, "#f00"),
    new NodeType("village", 13, "#f00"),
    new NodeType("hamlet", 14, "#f00"),
    new NodeType("suburb", 13, "#f00"))
}