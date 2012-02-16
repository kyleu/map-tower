package maptower.data

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.casbah.Imports._
import maptower.map._

class GameDao(dbName: String, enableStats: Boolean) extends BaseDao(dbName, enableStats) {
  def index {
  }
}