package maptower

package object data {
  var dbNamePrefix: String = "maptower"
  val enableTrace = true

  implicit lazy val osmDao = new OsmDao(dbNamePrefix + "-osm", enableTrace)
  implicit lazy val mapDao = new MapDao(dbNamePrefix + "-map", enableTrace)
  implicit lazy val gameDao = new GameDao(dbNamePrefix + "-game", enableTrace)
  implicit lazy val tileDao = new TileDao(dbNamePrefix + "-tile", enableTrace)

  def getDao(db: String) = {
    db match {
      case ("map") => mapDao
      case ("osm") => osmDao
      case ("tile") => tileDao
      case ("game") => gameDao
      case (_) => { println(db); mapDao }
    }
  }

}