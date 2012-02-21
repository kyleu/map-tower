package maptower.map.osm

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._
import maptower.map.{ Point, Way, Tags }

object OsmWay {
  val empty = new OsmWay(0, Seq[Int](), Map[String, String]())

  def apply() = empty

  def apply(obj: Obj) = {
    val nodeRefs: ObjList = obj.as[BasicDBList]("nodes")
    val nodeIds = nodeRefs.map(_.asInstanceOf[Int])
    new OsmWay(obj.as[Int]("osmId"), nodeIds, Tags(obj))
  }

  private val categoryPrefixes = "highway,tourism,railway,leisure,landuse,amenity".split(",")
  private val categoryValuePrefixes = "tourism,amenity,leisure,railway".split(",")
}

case class OsmWay(osmId: Int, nodeIds: Seq[Int], tags: Map[String, String] = Map.empty) extends Tags {
  def asWay(referencedOsmNodes: Seq[OsmNode]): Way = {
    val nodes = referencedOsmNodes map { n => (n.osmId -> n.loc) }
    val nodeMap = nodes.toMap
    val points = nodeIds flatMap (id => nodeMap.get(id))

    lazy val category = matchFirst(OsmWay.categoryPrefixes) match {
      case (k, v) if (OsmWay.categoryValuePrefixes.contains(k)) => v
      case (k, v) => k + ":" + v
    }

    new Way(osmId, tags.get("name").getOrElse("Unknown"), category, points, trimmedTags(OsmWay.categoryPrefixes))
  }
}
