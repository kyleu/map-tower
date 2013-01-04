package maptower.map.osm

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._
import maptower.map._

object OsmNodeHelper {
  def apply(obj: Obj): OsmNode = {
    var loc = obj.as[MongoDBList]("loc")
    new OsmNode(obj.as[Int]("osmId"), PointHelper(loc), OsmTags(obj))
  }
}

case class OsmNode(osmId: Int, loc: Point, tags: Map[String, String] = Map())