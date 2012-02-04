package data

import org.junit.Test
import org.junit.Assert._
import com.mongodb.casbah.Imports._

class DataTests {
  @Test
  def xmlLoad {
    DataManager.wipe("maptower")
    OsmImporter("maptower", "data/atlanta.osm")
    DataManager.index("maptower")
  }

  @Test
  def mondoPersist {}
}