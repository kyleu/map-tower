package maptower.game

import maptower.map.Point

object GameType {
  val types = Map(
    "atlanta" -> new GameType("atlanta", "Downtown Atlanta", new Point(-84.3856, 33.7612), 16),
    "eugene" -> new GameType("eugene", "Eugene, Oregon", new Point(0, 0), 16))
}

case class GameType(code: String, name: String, initialCenter: Point, initialZoom: Int)