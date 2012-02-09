package maptower.data

trait MongoClient {
  val dbName: String
  lazy val osmDao = new OsmDao(dbName + "-osm")
  lazy val mapDao = new MapDao(dbName + "-map")
  lazy val tileDao = new TileDao(dbName + "-tile")
}