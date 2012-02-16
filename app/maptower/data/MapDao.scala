package maptower.data

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.casbah.Imports._
import maptower.map._

class MapDao(dbName: String, enableTrace: Boolean) extends BaseDao(dbName, enableTrace) {
  override def ensureIndexes {
    mongoDb("node").createIndex(Obj("osmId" -> 1))
    mongoDb("node").createIndex(Obj("tags.k" -> 1))
    mongoDb("node").createIndex(Obj("loc" -> "2d"))

    mongoDb("way").createIndex(Obj("osmId" -> 1))
    mongoDb("way").createIndex(Obj("tags.k" -> 1))
    mongoDb("way").createIndex(Obj("points" -> "2d"))
  }

  def nodesNear(p: Point) = mongoDb("node") find (Obj("loc" -> near(p))) map (Node(_))
  def nodesWithin(b: Bounds) = mongoDb("node") find (Obj("loc" -> within(b))) map (Node(_))

  def waysIntersecting(b: Bounds, trimmed: Boolean = false) = mongoDb("way") find (Obj("points" -> within(b))) flatMap (p => if (trimmed) Way(p).trimmed(b) else List(Way(p)))
}