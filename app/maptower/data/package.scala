package maptower

package object data {
  var dbName: String = "maptower"
  val enableStats = true
  implicit lazy val osmDao = new OsmDao(dbName + "-osm", enableStats)
  implicit lazy val mapDao = new MapDao(dbName + "-map", enableStats)
  implicit lazy val gameDao = new GameDao(dbName + "-game", enableStats)
  implicit lazy val tileDao = new TileDao(dbName + "-tile", enableStats)
}