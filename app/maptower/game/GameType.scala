package maptower.game

import maptower.map.Point

object GameTypeHelper {
  val types = Map(
    "atlanta" -> new GameType("atlanta", "Downtown Atlanta", new Point(-84.3856, 33.7612), 16, new Point(-84.3856, 33.7612), new Point(-84.3856, 33.7612)),
    "eugene" -> new GameType("eugene", "Eugene, Oregon", new Point(-123.086667, 44.051944), 14, new Point(-123.140173, 44.017879), new Point(-123.033228, 44.086044)))
}

case class GameType(
    code: String,
    name: String,
    initialCenter: Point,
    initialZoom: Int,
    boundsUpperLeft: Point,
    boundsBottomRight: Point) {
  val nodes = null
  val ways = null
}