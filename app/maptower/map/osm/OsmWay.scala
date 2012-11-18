package maptower.map.osm

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._
import maptower.map._

object OsmWay {
  val empty = new OsmWay(0, Seq[Int](), Map[String, String]())

  def apply() = empty

  def apply(obj: Obj) = {
    val nodeRefs: ObjList = obj.as[MongoDBList]("nodes")
    val nodeIds = nodeRefs.map(_.asInstanceOf[Int])
    new OsmWay(obj.as[Int]("osmId"), nodeIds, OsmTags(obj))
  }
}

case class OsmWay(osmId: Int, nodeIds: Seq[Int], tags: Map[String, String] = Map.empty)
