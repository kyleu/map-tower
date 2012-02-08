package map

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._

object Node {
  val empty = new Node(0, "Default Node", new Point(0, 0))

  def apply(obj: Obj): Node = {
    var loc = obj.as[BasicDBList]("loc")
    new Node(obj.as[Int]("osmid"), obj.as[String]("name"), Point(loc), Tags.load(obj))
  }
}

case class Node(osmId: Int, name: String, loc: Point, tags: Map[String, String] = Map()) extends Tags {

}