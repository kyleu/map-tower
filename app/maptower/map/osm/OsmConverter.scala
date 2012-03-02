package maptower.map.osm

import scala.xml.pull._
import scala.io.Source
import java.io.File
import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.casbah.Imports._
import maptower.data._
import maptower.map._

object OsmConverter {
  def apply() {
    OsmNodeConverter()
    OsmWayConverter()
  }
}

object OsmNodeConverter {
  def apply() {
    val osmNodes = osmDao.mongoDb("osmnode").find(Obj("tags.k" -> "name")) map (OsmNode(_))
    val nodes = osmNodes map { apply(_) }

    val nodeCollection = mapDao.mongoDb("node")
    var nodeCount = 0
    for (node <- nodes) {
      if (node.category == "unknown") {
        println(node.category + "/" + node.subcategory)
      } else {
        nodeCollection.insert(node.toObj)
        nodeCount += 1
      }
    }
    ("Inserted %s nodes." format nodeCount)
  }

  private val ignoredNodeOsmTags = Array("highway", "exit_to", "ref", "power", "source", "name", "bicycle", "crossing", "ped_buffer", "railway", "FIXME", "barrier", "ele", "gnis:Class", "gnis:County", "gnis:County_num", "gnis:ST_alpha", "gnis:ST_num", "gnis:id", "import_uuid", "is_in", "place", "census:population", "population", "created_by", "amenity", "old_name", "denomination", "religion", "addr:state", "gnis:county_id", "gnis:created", "gnis:edited", "gnis:feature_id", "leisure", "tourism", "natural", "landuse", "man_made", "waterway", "gnis:county_name", "building", "aeroway", "gnis:feature_type", "source_ref", "note", "operator", "url", "addr:housenumber", "addr:street", "shop", "alt_name", "cuisine", "website", "emergency", "traffic_calming", "access", "fuel:lpg", "phone", "tower:type", "addr:housename", "addr:postcode", "designation")

  def apply(n: OsmNode) = {
    val extractor = new OsmTagExtractor(n.tags)
    new Node(n.osmId, extractor.name.get, extractor.category, extractor.subcategory, n.loc, extractor.note)
  }
}

object OsmWayConverter {
  def apply() {
    val wayCollection = mapDao.mongoDb("way")
    var wayCount = 0
    val osmWays = osmDao.mongoDb("osmway") find () map (OsmWay(_))
    val ways = osmWays map { osmWay =>
      val referencedPoints = osmDao.getNodes(osmWay.nodeIds).toSeq
      val way = apply(osmWay, referencedPoints)
      wayCount += 1
      way
    }
    for (way <- ways) {
      wayCollection.insert(way.toObj)
    }
    println("Inserted %s ways." format wayCount)

    var relationCount = 0
    val osmRelations = osmDao.mongoDb("osmrelation") find () map (OsmRelation(_))
    for (relation <- osmRelations) {
      relationCount += 1
      //println(relation.members.size, relation.tags)
    }
    println("Processed %s relations." format relationCount)
  }

  def apply(w: OsmWay, referencedOsmNodes: Seq[OsmNode]) = {
    val nodes = referencedOsmNodes map { n => (n.osmId -> n.loc) }
    val nodeMap = nodes.toMap
    val points = w.nodeIds flatMap (id => nodeMap.get(id))

    val extractor = new OsmTagExtractor(w.tags)

    new Way(w.osmId, extractor.name.getOrElse("Unnamed"), extractor.category, extractor.subcategory, points, extractor.note)
  }
}

// Mutable and hacky. Don't care.
class OsmTagExtractor(tags: Map[String, String]) {
  var name = tags.get("name")
  var category = ""
  var subcategory = ""
  val unusedTags = collection.mutable.HashMap[String, String]()
  lazy val note = unusedTags map { case (k, v) => k + " => " + v } mkString ", "

  private val ignoredPrefixes = "xxxxxxx,yyyyyyy,zzzzzz".split(",")
  private val categoryPrefixes = "shop,man_made,tourism,highway,place,aeroway,railway,natural,building,leisure,landuse,waterway,historic,sport,amenity".split(",")
  private val categoryValuePrefixes = "tourism,amenity,leisure,railway".split(",")

  tags foreach {
    case (k, v) if (ignoredPrefixes.contains(k)) => //noop
    case (k, v) if (categoryPrefixes.contains(k)) => {
      if (!category.isEmpty()) {
        println("Matches categories %s and %s." format ((category, subcategory), (k, v)))
      }
      if (categoryValuePrefixes.contains(k)) {
        category = v
        subcategory = "standard"
      } else {
        category = k
        subcategory = v
      }
    }
    case (k, v) if (k == "name") => name = Some(v)
    case (k, v) => unusedTags += (k -> v)
  }
}