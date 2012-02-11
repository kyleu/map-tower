package maptower.data

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.casbah.Imports._
import maptower.map.{ Point, Bounds }

object BaseDao {

}

class BaseDao {
  protected def within(b: Bounds) = Obj("$within" -> Obj("$box" -> Array(Array(b.min.x, b.min.y), Array(b.max.x, b.max.y))))
  protected def near(p: Point) = Obj("$near" -> Array(p.x, p.y))

}