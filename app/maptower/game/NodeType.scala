package maptower.game
import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.casbah.Imports._
import maptower.data._

object NodeType {
}

case class NodeType(code: String, zoomLevel: Int, color: String) {
  def toObj = Obj("code" -> code, "zoomLevel" -> zoomLevel, "color" -> color)
}
