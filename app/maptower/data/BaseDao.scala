package maptower.data

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.casbah.Imports._
import maptower.map.{ Point, Bounds }

object BaseDao {

}

class BaseDao {
  protected def within(b: Bounds) = Obj("$within" -> Obj("$box" -> Array(Array(b.ul.x, b.ul.y), Array(b.br.x, b.br.y))))
  protected def near(p: Point) = Obj("$near" -> Array(p.x, p.y))

}