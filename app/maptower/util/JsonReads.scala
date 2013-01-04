package maptower.util

import play.api.libs.json._
import play.api.libs.functional.syntax._

import maptower.game._
import maptower.map.osm._
import maptower.map._

object JsonReads {
  implicit val readsPoint = Json.reads[Point]
  implicit val readsBounds = Json.reads[Bounds]
  implicit val readsNode = Json.reads[Node]
  implicit val readsWay = Json.reads[Way]

  implicit val readsOsmNode = Json.reads[OsmNode]
  implicit val readsOsmRelationMember = Json.reads[OsmRelationMember]
  implicit val readsOsmRelation = Json.reads[OsmRelation]
  implicit val readsOsmWay = Json.reads[OsmWay]

  implicit val readsGameType = Json.reads[GameType]
  implicit val readsGameEvent = Json.reads[GameEvent]
}