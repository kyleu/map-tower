package maptower.data

import org.junit.Test
import org.junit.Assert._
import com.mongodb.casbah.Imports._

class OsmDataTests extends MongoClient {
  val dbName = "test"

  @Test
  def xmlLoad {
    osmDao.wipe
    OsmImporter.load(osmDao, "data/eugene.osm")
    osmDao.index

    mapDao.wipe
    OsmImporter.convert(osmDao, mapDao)
    mapDao.index
  }
}