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
    val osmNodes = osmDao.mongoDb("osmnode").find(Obj("tags.k" -> "name")) map (OsmNodeHelper(_))
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
    val extractor = TagExtractor(n.tags)
    new Node(n.osmId, extractor.name.get, extractor.category, extractor.subcategory, n.loc, extractor.note)
  }
}

object OsmWayConverter {
  def apply() {
    val wayCollection = mapDao.mongoDb("way")
    var wayCount = 0
    val osmWays = osmDao.mongoDb("osmway") find () map (OsmWayHelper(_))
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
    val osmRelations = osmDao.mongoDb("osmrelation") find () map (OsmRelationHelper(_))
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

    val extractor = TagExtractor(w.tags)

    new Way(w.osmId, extractor.name.getOrElse("Unnamed"), extractor.category, extractor.subcategory, points, extractor.note)
  }
}

private object TagExtractor {
  val ignoreKeys = Array("addr:state", "ele", "import_uuid", "created_by")
  val ignoredKeyPrefixes = Array("gnis:", "tiger:")

  val categoryPrefixPriorities = Map("shop" -> 10, "man_made" -> 10, "tourism" -> 10, "highway" -> 9,
    "place" -> 10, "aeroway" -> 9, "railway" -> 8, "natural" -> 10, "building" -> 3, "leisure" -> 9, "landuse" -> 5,
    "waterway" -> 11, "historic" -> 8, "sport" -> 11, "amenity" -> 1)

  def priority(category: String) = categoryPrefixPriorities(category)
  val categoryValuePrefixes = "tourism,amenity,leisure,railway".split(",")

  def apply(tags: Map[String, String]) = {
    var name = tags.get("name")
    var category = ""
    var subcategory = ""
    val unusedTags = collection.mutable.HashMap[String, String]()
    var currentPriority: Int = -1

    tags foreach {
      case (k, v) if (ignoredKeyPrefixes.contains(k)) => //noop
      case (k, v) if (ignoredKeyPrefixes.find(x => k.startsWith(x)).isDefined) => //noop
      case (k, v) if (k == "name") => name = Some(v)
      case (k, v) if (categoryPrefixPriorities.keySet.contains(k)) => {
        priority(k) match {
          case (newPriority) if (currentPriority < newPriority) => {
            currentPriority = newPriority
            category = k
            subcategory = v
          }
          case (newPriority) if (currentPriority > newPriority) => // no-op
          case (newPriority) => println("Categories %s and %s share priority %s." format ((category, subcategory), (k, v), newPriority))
        }
      }
      case (k, v) => unusedTags += (k -> v)
    }
    new ExtractedTags(name, category, subcategory, unusedTags.toMap)
  }
}

case class ExtractedTags(name: Option[String], category: String, subcategory: String, unusedTags: Map[String, String]) {
  lazy val note = unusedTags map { case (k, v) => k + " => " + v } mkString ", "
}