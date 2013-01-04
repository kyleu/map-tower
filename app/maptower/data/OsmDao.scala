package maptower.data;

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.casbah.Imports._
import maptower.map._
import maptower.map.osm._

class OsmDao(dbName: String, enableTrace: Boolean) extends BaseDao(dbName, enableTrace) {
  override def ensureIndexes {
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

  def nodesNear(p: Point) = mongoDb("osmnode") find (Obj("loc" -> near(p))) map (OsmNodeHelper(_))
  def nodesWithin(b: Bounds) = mongoDb("osmnode") find (Obj("loc" -> within(b))) map (OsmNodeHelper(_))
  def getNodes(nodeIds: Seq[Int]) = mongoDb("osmnode") find ("osmId" $in nodeIds) map (OsmNodeHelper(_))

  def waysContainingNodes(nodeOsmIds: Array[Int]) = mongoDb("osmway") find ("nodes" $in nodeOsmIds) map (OsmWayHelper(_))

  def relationsContainingNodes(nodeOsmIds: Array[Int]) = mongoDb("osmrelation").find(Obj("members.type" -> "node", "members.ref" -> nodeOsmIds)) map (OsmRelationHelper(_)) toSeq
  def relationsContainingWays(wayOsmIds: Array[Int]) = mongoDb("osmrelation").find(Obj("members.type" -> "way", "members.ref" -> Obj("$in" -> ObjList(wayOsmIds)))) map (OsmRelationHelper(_)) toSeq

}