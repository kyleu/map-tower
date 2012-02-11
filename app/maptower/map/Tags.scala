package maptower.map

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._

object Tags {
  def apply(node: Obj) = {
    val nodeTags: ObjList = node.as[BasicDBList]("tags")
    nodeTags map { nodeTag =>
      var nodeTagObj: Obj = nodeTag.asInstanceOf[BasicDBObject]
      (nodeTagObj.as[String]("k"), nodeTagObj.as[String]("v"))
    } toMap
  }
}

trait Tags {
  val tags: Map[String, String]

  protected def trimmedTags(forbidden: Array[String]) = tags filter ((t) => !(forbidden.contains(t._1) || t._1 == "name"))
  protected def tagsObjList = tags map { tag => Obj("k" -> tag._1, "v" -> tag._2) }
  protected def matchFirst(options: Array[String]) = tags.foldLeft(("", "")) { (l, r) =>
    r match {
      case (k, v) if (options.contains(k)) =>
        if (l._1 != "") {
          println("Matches both %s and %s." format (l, (k, v)))
        }
        (k, v)
      case (_, _) => l
    }
  }
}