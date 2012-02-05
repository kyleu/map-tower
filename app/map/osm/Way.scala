package map.osm

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._

object Way {
  val empty = new Way(Seq[Int](), Map[String, String]())

  def apply() = empty

  def apply(obj: Obj): Way = {
    val nodeRefs: ObjList = obj.as[BasicDBList]("nodes")
    val nodeIds = nodeRefs.map(_.asInstanceOf[Int])
    new Way(nodeIds, Tags.load(obj))
  }
}

case class Way(nodeIds: Seq[Int], tags: Map[String, String] = Map.empty) {
}
