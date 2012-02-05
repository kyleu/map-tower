package map

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._

object Point {
  def empty = new Point(0, 0)
  def apply(objList: BasicDBList): Point = {
    new Point(objList.get(1).asInstanceOf[Double], objList.get(0).asInstanceOf[Double])
  }
}

case class Point(x: Double, y: Double) {
  def lat = y
  def lng = x
}