package map

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._

object Tags {
  def load(node: Obj) = {
    val nodeTags: ObjList = node.as[BasicDBList]("tags")
    val tags = scala.collection.mutable.Map[String, String]()
    for (nodeTag <- nodeTags) yield {
      var nodeTagObj: Obj = nodeTag.asInstanceOf[BasicDBObject]
      tags += (nodeTagObj.as[String]("k") -> nodeTagObj.as[String]("v"))
    }
    tags.toMap
  }
}