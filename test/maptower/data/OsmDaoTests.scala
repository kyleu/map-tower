package maptower.data

import org.junit.Test
import org.junit.Assert._
import com.mongodb.casbah.Imports._
import maptower.map.osm.{ OsmImporter, OsmConverter }

class OsmDataTests {
  maptower.data.dbNamePrefix = "test"

  @Test
  def xmlLoad {
    osmDao.wipe
    OsmImporter("data/eugene.osm")
    osmDao.ensureIndexes

    mapDao.wipe
    OsmConverter()
    mapDao.ensureIndexes
  }
}