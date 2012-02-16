package maptower.data

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.casbah.Imports._
import maptower.map._

class GameDao(dbName: String, enableTrace: Boolean) extends BaseDao(dbName, enableTrace) {
  def ensureIndexes {
  }
}