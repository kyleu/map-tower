package maptower.util

import play.api.libs.json._
import play.api.libs.functional.syntax._
import maptower.game._
import maptower.map.osm._
import maptower.map._
import maptower.game.GameType

object JsonWrites {
  implicit val writesPoint = Json.writes[Point]
  implicit val writesBounds = Json.writes[Bounds]
  implicit val writesNode = Json.writes[Node]
  implicit val writesWay = Json.writes[Way]

  implicit val writesOsmNode = Json.writes[OsmNode]
  implicit val writesOsmRelationMember = Json.writes[OsmRelationMember]
  implicit val writesOsmRelation = Json.writes[OsmRelation]
  implicit val writesOsmWay = Json.writes[OsmWay]

  implicit val writesGameType = Json.writes[GameType]
  implicit val writesGameEvent = Json.writes[GameEvent]
  implicit val writesSpawn = Json.writes[Spawn]
}