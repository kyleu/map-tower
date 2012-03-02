package maptower.game
import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.casbah.Imports._

object WayType {
  private val polygonCategories = Map(
    ("building" -> true),
    ("landuse" -> true),
    ("leisure" -> true),
    ("amenity" -> true),
    ("shop" -> true),
    ("tourism" -> true),
    ("historic" -> true),
    ("area" -> true),
    ("military" -> true),
    ("sport" -> true),
    ("natural" -> true),
    ("natural:coastline" -> false),
    ("natural:cliff" -> false),
    ("waterway" -> false),
    ("waterway:dock" -> true),
    ("railway" -> false),
    ("railway:turntable" -> true),
    ("aeroway" -> false),
    ("aeroway:terminal" -> true),
    ("aeroway:apron" -> true),
    ("power" -> false),
    ("power:substation" -> true),
    ("power:pier" -> true),
    ("power:wastewater_plant" -> true))
}

case class WayType(code: String, zoomLevel: Int, color: String) {
  lazy val isPolygon = {
    WayType.polygonCategories.get(code) match {
      case Some(eligible) => eligible
      case None => if (code.indexOf(':') > 0) {
        val rootCategory = code.substring(0, code.indexOf(':'))
        WayType.polygonCategories.get(rootCategory) match {
          case Some(eligible) => eligible
          case None => false
        }
      }
    }
  }
  def toObj = Obj("code" -> code, "isPolygon" -> isPolygon)
}
