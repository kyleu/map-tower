package map.osm

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._
import map.Point

object Node {
  val empty = new Node(0, new Point(0, 0))

  def apply(obj: Obj): Node = {
    var loc = obj.as[BasicDBList]("loc")
    new Node(obj.as[Int]("osmid"), Point(loc), Tags.load(obj))
  }
}

case class Node(osmId: Int, loc: Point, tags: Map[String, String] = Map())