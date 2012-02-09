package maptower.map.osm

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._
import maptower.map.{ Point, Tags, Node, Bounds }

object OsmNode {
  val empty = new OsmNode(0, new Point(0, 0))

  def apply(obj: Obj): OsmNode = {
    var loc = obj.as[BasicDBList]("loc")
    new OsmNode(obj.as[Int]("osmId"), Point(loc), Tags.load(obj))
  }

  private val categoryPrefixes = "amenity,shop,man_made,tourism,highway,place,aeroway,railway,natural,building,leisure,landuse,waterway,historic,sport,".split(",")
}

case class OsmNode(osmId: Int, loc: Point, tags: Map[String, String] = Map()) extends Tags {
  def category = tags.foldLeft(("unknown", "unknown")) { (l, r) =>
    r match {
      case (k, v) if (OsmNode.categoryPrefixes.contains(k)) => (k, v)
      case (_, _) => l
    }
  }

  def asNode = new Node(osmId, tags.get("name").get, category._1 + ":" + category._2, loc, trimmedTags(OsmNode.categoryPrefixes))
}
