package maptower.data

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.casbah.Imports._
import maptower.map._
import maptower.map.osm._

class MapDao(val dbName: String) extends BaseDao {
  lazy val mongoDb = MongoConnection()(dbName)

  def wipe() {
    mongoDb.dropDatabase()
  }

  def index() {
    mongoDb("node").createIndex(Obj("osmId" -> 1))
    mongoDb("node").createIndex(Obj("tags.k" -> 1))
    mongoDb("node").createIndex(Obj("loc" -> "2d"))

    mongoDb("way").createIndex(Obj("osmId" -> 1))
    mongoDb("way").createIndex(Obj("tags.k" -> 1))
    mongoDb("way").createIndex(Obj("points" -> "2d"))
  }

  def nodesNear(p: Point) = mongoDb("node") find (Obj("loc" -> near(p))) map (Node(_))
  def nodesWithin(b: Bounds) = mongoDb("node") find (Obj("loc" -> within(b))) map (Node(_))

  def waysIntersecting(b: Bounds) = mongoDb("way") find (Obj("points" -> within(b))) map (Way(_))

  def relationsContainingNodes(nodeOsmIds: Array[Int]) = mongoDb("osmrelation").find(Obj("members.type" -> "node", "members.ref" -> nodeOsmIds)) map (OsmRelation(_)) toSeq
  def relationsContainingWays(wayOsmIds: Array[Int]) = mongoDb("osmrelation").find(Obj("members.type" -> "way", "members.ref" -> Obj("$in" -> ObjList(wayOsmIds)))) map (OsmRelation(_)) toSeq

  def loadOsm(osmDao: OsmDao) {
    val osmNodes = osmDao.mongoDb("osmnode").find(Obj("tags.k" -> "name")) map (OsmNode(_))
    val nodes = osmNodes map { _.asNode }

    val nodeCollection = mongoDb("node")
    nodeCollection.drop
    var nodeCount = 0
    for (node <- nodes) {
      if (node.typ == "unknown") {
        println(node)
      } else {
        nodeCollection.insert(node.toObj)
        nodeCount += 1
      }
    }
    println("Inserted %s nodes." format nodeCount)

    val wayCollection = mongoDb("way")
    var wayCount = 0
    println(osmDao.mongoDb("osmway").count)
    val osmWays = osmDao.mongoDb("osmway") find () map (OsmWay(_))
    //println(osmWays.size)
    val ways = osmWays map { osmWay =>
      val referencedPoints = osmDao.getNodes(osmWay.nodeIds)
      val way = osmWay.asWay(referencedPoints)
      println(way)
      wayCount += 1
      way
    }
    println("Inserted %s ways." format wayCount)

    val osmRelations = osmDao.mongoDb("osmrelation")
  }
}