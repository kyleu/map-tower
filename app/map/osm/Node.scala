package map.osm

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._
import map.Point

object Node {
  val empty = new Node(new Point(0, 0))

  def apply(x: Double, y: Double) = new Node(new Point(x, y))

  def apply(obj: Obj): Node = {
    var loc = obj.as[BasicDBList]("loc")
    new Node(Point(loc), Tags.load(obj))
  }
}

case class Node(loc: Point, tags: Map[String, String] = Map())