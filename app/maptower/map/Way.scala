package maptower.map

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._

object WayHelper {
  def apply(obj: Obj): Way = {
    var pointsObjList: ObjList = obj.as[MongoDBList]("points")
    var points = pointsObjList map (p => PointHelper(p.asInstanceOf[BasicDBList]))
    new Way(obj.as[Int]("osmId"), obj.as[String]("name"), obj.as[String]("category"), obj.as[String]("subcategory"), points, obj.as[String]("note"))
  }
}

case class Way(osmId: Int, name: String, category: String, subcategory: String, points: Seq[Point], note: String = "...") {
  def toObj = Obj("osmId" -> osmId, "name" -> name, "category" -> category, "subcategory" -> subcategory, "points" -> points.map(_.toObj), "note" -> note)
  def trimmed(b: Bounds) = {
    var ret = List[Way]()
    var idx = -1
    var min = -1
    var max = -1
    for (point <- points) {
      idx += 1
      if (b.contains(point)) {
        if (min == -1) min = idx
        if (max != -1 && idx - max > 1) {
          ret = ret ::: List(new Way(osmId, name, category, subcategory, trimPlusOne(points, min, max), note))
          min = idx
        }
        max = idx
      }
    }
    ret ::: List(new Way(osmId, name, category, subcategory, trimPlusOne(points, min, max), note))
  }

  private def trimPlusOne(points: Seq[Point], min: Int, maxInclusive: Int) = {
    points.slice(if (min == 0) min else min - 1, if (maxInclusive == points.length - 1) maxInclusive + 1 else maxInclusive + 2)
  }
}