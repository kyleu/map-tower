package maptower.data

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.casbah.Imports._
import maptower.map.{ Point, Bounds }

object BaseDao {
  def fromJson(json: String) = {
    com.mongodb.util.JSON.parse(json).asInstanceOf[DBObject]
  }

  protected lazy val conn = MongoConnection()
}

abstract class BaseDao(dbName: String, enableTrace: Boolean) {
  val mongoDb = BaseDao.conn(dbName)
  if (enableTrace) {
  }

  def wipe {
    mongoDb.dropDatabase()
  }

  def ensureIndexes

  def stats = {
    mongoDb.stats
  }

  def getInfo = {
    val collections = mongoDb.collectionNames.toArray[String] map (name => (name, mongoDb(name).count.toInt))
    collections.toMap
  }

  protected def within(b: Bounds) = Obj("$within" -> Obj("$box" -> Array(Array(b.min.x, b.min.y), Array(b.max.x, b.max.y))))
  protected def near(p: Point) = Obj("$near" -> Array(p.x, p.y))

}