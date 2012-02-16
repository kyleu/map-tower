package maptower

package object data {
  var dbName: String = "maptower"
  val enableTrace = true
  implicit lazy val osmDao = new OsmDao(dbName + "-osm", enableTrace)
  implicit lazy val mapDao = new MapDao(dbName + "-map", enableTrace)
  implicit lazy val gameDao = new GameDao(dbName + "-game", enableTrace)
  implicit lazy val tileDao = new TileDao(dbName + "-tile", enableTrace)
}