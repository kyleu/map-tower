package maptower.data

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.casbah.Imports._
import maptower.map.{ Point, Bounds }

object BaseDao {

}

abstract class BaseDao(dbName: String, enableTrace: Boolean) {
  val mongoDb = MongoConnection()(dbName)

  if (enableTrace) {
  }

  def wipe {
    mongoDb.dropDatabase()
  }

  def ensureIndexes

  def stats = {
    mongoDb.stats
  }

  def importJson(json: String) = {

  }

  protected def within(b: Bounds) = Obj("$within" -> Obj("$box" -> Array(Array(b.min.x, b.min.y), Array(b.max.x, b.max.y))))
  protected def near(p: Point) = Obj("$near" -> Array(p.x, p.y))

}