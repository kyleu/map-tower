package map.osm

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._

object Point {
  val empty = new Point(0, 0)

  def apply() = empty

  def apply(node: Obj): Point = {
    var loc = node.as[BasicDBList]("loc")
    new Point(loc.get(1).asInstanceOf[Double], loc.get(0).asInstanceOf[Double], Tags.load(node))
  }
}

case class Point(x: Double, y: Double, tags: Map[String, String] = Map.empty) {
  def lat = y
  def lng = x
}