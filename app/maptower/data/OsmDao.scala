package maptower.data;

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.casbah.Imports._
import maptower.map._
import maptower.map.osm._

class OsmDao(val dbName: String) extends BaseDao {
  lazy val mongoDb = MongoConnection()(dbName)

  def wipe() {
    mongoDb.dropDatabase()
  }

  def index() {
    mongoDb("osmnode").createIndex(Obj("osmId" -> 1))
    mongoDb("osmnode").createIndex(Obj("tags.k" -> 1))
    mongoDb("osmnode").createIndex(Obj("loc" -> "2d"))

    mongoDb("osmway").createIndex(Obj("osmId" -> 1))
    mongoDb("osmway").createIndex(Obj("tags.k" -> 1))
    mongoDb("osmway").createIndex(Obj("nodes" -> 1))

    mongoDb("osmrelation").createIndex(Obj("osmId" -> 1))
    mongoDb("osmrelation").createIndex(Obj("tags.k" -> 1))
    mongoDb("osmrelation").createIndex(Obj("members.type" -> 1))
    mongoDb("osmrelation").createIndex(Obj("members.ref" -> 1))
  }

  def nodesNear(p: Point) = mongoDb("osmnode") find (Obj("loc" -> near(p))) map (OsmNode(_))
  def nodesWithin(b: Bounds) = mongoDb("osmnode") find (Obj("loc" -> within(b))) map (OsmNode(_))
  def getNodes(nodeIds: Seq[Int]) = mongoDb("osmnode") find ("loc" $in nodeIds) map (OsmNode(_))

  def waysContainingNodes(nodeOsmIds: Array[Int]) = mongoDb("osmway") find ("nodes" $in nodeOsmIds) map (OsmWay(_))

  def relationsContainingNodes(nodeOsmIds: Array[Int]) = mongoDb("osmrelation").find(Obj("members.type" -> "node", "members.ref" -> nodeOsmIds)) map (OsmRelation(_)) toSeq
  def relationsContainingWays(wayOsmIds: Array[Int]) = mongoDb("osmrelation").find(Obj("members.type" -> "way", "members.ref" -> Obj("$in" -> ObjList(wayOsmIds)))) map (OsmRelation(_)) toSeq

}