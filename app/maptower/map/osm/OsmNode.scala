package maptower.map.osm

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._
import maptower.map.{ Point, Tags, Node, Bounds }

object OsmNode {
  val empty = new OsmNode(0, new Point(0, 0))

  def apply(obj: Obj): OsmNode = {
    var loc = obj.as[BasicDBList]("loc")
    new OsmNode(obj.as[Int]("osmId"), Point(loc), Tags(obj))
  }

  private val categoryPrefixes = "shop,man_made,tourism,highway,place,aeroway,railway,natural,building,leisure,landuse,waterway,historic,sport,amenity".split(",")
}

case class OsmNode(osmId: Int, loc: Point, tags: Map[String, String] = Map()) extends Tags {
  lazy val category = matchFirst(OsmNode.categoryPrefixes)
  def asNode = new Node(osmId, tags.get("name").get, category._1, category._2, loc, trimmedTags(OsmNode.categoryPrefixes))
}
