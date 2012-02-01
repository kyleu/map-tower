package data;

import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject

object DataManager {
  def wipe(db: String) {
    val mongoDb = getConn(db)
    mongoDb.dropDatabase()
  }

  def index(db: String) {
    val mongoDb = getConn(db)
    mongoDb("node").createIndex(MongoDBObject("osmid" -> 1))
    mongoDb("node").createIndex(MongoDBObject("tags.k" -> 1))
    mongoDb("node").createIndex(MongoDBObject("loc" -> "2d"))

    mongoDb("way").createIndex(MongoDBObject("osmid" -> 1))
    mongoDb("way").createIndex(MongoDBObject("tags.k" -> 1))
    mongoDb("way").createIndex(MongoDBObject("nodes" -> 1))

    mongoDb("relation").createIndex(MongoDBObject("osmid" -> 1))
    mongoDb("relation").createIndex(MongoDBObject("tags.k" -> 1))
    mongoDb("relation").createIndex(MongoDBObject("members.type" -> 1))
    mongoDb("relation").createIndex(MongoDBObject("members.ref" -> 1))
  }

  def getConn(db: String) = {
    val mongoConn = MongoConnection()
    mongoConn(db)
  }
}