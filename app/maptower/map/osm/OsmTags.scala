package maptower.map.osm

import com.mongodb.casbah.commons.{ MongoDBObject => Obj, MongoDBList => ObjList }
import com.mongodb.{ BasicDBList, BasicDBObject }
import com.mongodb.casbah.Imports._

object OsmTags {
  def apply(node: Obj) = {
    val nodeTags: ObjList = node.as[MongoDBList]("tags")
    nodeTags map { nodeTag =>
      var nodeTagObj: Obj = nodeTag.asInstanceOf[BasicDBObject]
      (nodeTagObj.as[String]("k"), nodeTagObj.as[String]("v"))
    } toMap
  }
}
