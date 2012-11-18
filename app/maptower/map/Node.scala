package maptower.map

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._

object Node {
  val empty = new Node(0, "The Middle of Nowhere", "unknown", "unknown", new Point(0, 0))

  def apply(obj: Obj): Node = {
    var loc = obj.as[MongoDBList]("loc")
    new Node(obj.as[Int]("osmId"), obj.as[String]("name"), obj.as[String]("category"), obj.as[String]("subcategory"), Point(loc), obj.as[String]("note"))
  }
}

case class Node(osmId: Int, name: String, category: String, subcategory: String, loc: Point, note: String = "...") {
  def toObj = Obj("osmId" -> osmId, "name" -> name, "category" -> category, "subcategory" -> subcategory, "loc" -> loc.toObj, "note" -> note)
}