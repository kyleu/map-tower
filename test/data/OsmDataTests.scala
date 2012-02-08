package data

import org.junit.Test
import org.junit.Assert._
import com.mongodb.casbah.Imports._

class OsmDataTests {
  val dbName = "test"

  @Test
  def xmlLoad {
    //    DataManager.wipe(dbName)
    //    OsmImporter(dbName, "data/eugene.osm")
    //    DataManager.index(dbName)
    DataManager.normalize(dbName)
  }
}