package data

import org.junit.Test
import org.junit.Assert._
import com.mongodb.casbah.Imports._

class DataTests {
  val dbName = "test"

  @Test
  def xmlLoad {
    DataManager.wipe(dbName)
    OsmImporter(dbName, "data/eugene.osm")
    DataManager.index(dbName)
  }

  @Test
  def pointPersist {

  }
}