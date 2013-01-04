package maptower.map

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._

object NodeHelper {
  def apply(obj: Obj): Node = {
    val loc = obj.as[MongoDBList]("loc")
    val p = PointHelper(loc)
    new Node(obj.as[Int]("osmId"), obj.as[String]("name"), obj.as[String]("category"), obj.as[String]("subcategory"), p, obj.as[String]("note"))
  }
}

case class Node(osmId: Int, name: String, category: String, subcategory: String, loc: Point, note: String = "...") {
  def toObj = Obj("osmId" -> osmId, "name" -> name, "category" -> category, "subcategory" -> subcategory, "loc" -> loc.toObj, "note" -> note)
}