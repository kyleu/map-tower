package maptower.map

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._

object Way {
  val empty = new Node(0, "The Middle of Nowhere", "unknown:unknown", new Point(0, 0))

  def apply(obj: Obj): Way = {
    var pointsObjList: ObjList = obj.as[BasicDBList]("points")
    var points = pointsObjList map (p => Point(p.asInstanceOf[BasicDBList]))
    new Way(obj.as[Int]("osmId"), obj.as[String]("name"), obj.as[String]("typ"), points, Tags.load(obj))
  }
}

case class Way(osmId: Int, name: String, typ: String, points: Seq[Point], tags: Map[String, String] = Map()) extends Tags {
  def toObj = Obj("osmId" -> osmId, "name" -> name, "typ" -> typ, "points" -> points.map(_.toObj), "tags" -> tagsObjList)
}