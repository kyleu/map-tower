package map.osm

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._

object Way {
  val empty = new Way(Seq[Int](), Map[String, String]())

  def apply() = empty

  def apply(way: Obj): Way = {
    val refs: ObjList = way.as[BasicDBList]("ref")
    val refIds = refs.map(_.asInstanceOf[Double].toInt)
    new Way(refIds, Tags.load(way))
  }
}

case class Way(nodeIds: Seq[Int], tags: Map[String, String] = Map.empty) {
}
