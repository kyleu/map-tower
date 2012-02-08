package map

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._

object Line {
  val empty = new Line(Seq[Point]())

  def apply() = empty

  def apply(obj: Obj) = {
    val pointList: ObjList = obj.as[BasicDBList]("points")
    val points = pointList map { p => Point(p.asInstanceOf[BasicDBList]) }
    new Line(points)
  }
}

case class Line(points: Seq[Point]) {
  override def toString = points.toString
}