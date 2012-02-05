package map.osm

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._

object Way {
  val empty = new Way(Seq[Int](), Map[String, String]())

  def apply() = empty

  def apply(obj: Obj): Way = {
    val nodeIds: ObjList = obj.as[BasicDBList]("nodes")
    val nodeOsmIds = nodeIds.map(_.asInstanceOf[Int])
    new Way(nodeOsmIds, Tags.load(obj))
  }
}

case class Way(nodeIds: Seq[Int], tags: Map[String, String] = Map.empty) {
}
