package maptower.data

import org.junit.Test
import org.junit.Assert._
import com.mongodb.casbah.Imports._
import maptower.map.osm.OsmImporter

class OsmDataTests extends MongoClient {
  val dbName = "test"

  @Test
  def xmlLoad {
    //    osmDao.wipe
    //    OsmImporter(osmDao, "data/eugene.osm")
    //    osmDao.index
    //
    //    mapDao.wipe
    //    mapDao.loadOsm(osmDao)
    //    mapDao.index
  }
}