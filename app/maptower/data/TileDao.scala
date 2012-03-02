package maptower.data

import java.io._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.gridfs.Imports._
import play.api.Logger
import java.util.Date

class TileDao(dbName: String, enableTrace: Boolean) extends BaseDao(dbName, enableTrace) {
  private val styleId = 998
  private val rootUrl = "http://%s.tile.cloudmade.com/0320d0049e1a4242bab7857cec8b343a/%s/256/".format("a", styleId)

  private lazy val mongoFs = GridFS(mongoDb)

  override def ensureIndexes {

  }

  def get(z: Int, x: Int, y: Int): GridFSDBFile = {
    val filename = "%s-%s-%s.png".format(z, y, x)
    mongoFs.findOne(filename) getOrElse {
      cache(filename)
      mongoFs.findOne(filename).get
    }
  }

  private def cache(filename: String) = {
    val start = new Date
    val url = rootUrl + filename.replace("-", "/")
    val uc = new java.net.URL(url).openConnection();
    val contentType = uc.getContentType();
    val contentLength = uc.getContentLength();
    if (contentType.startsWith("text/") || contentLength == -1) {
      throw new java.io.IOException("This is not a binary file. %s" format url);
    }

    val input = new BufferedInputStream(uc.getInputStream());

    mongoFs(input) { fh =>
      fh.filename = filename
      fh.contentType = contentType
    }
    Logger.info("Cached %s as %s in %sms.".format(filename, contentType, new Date().getTime() - start.getTime()))
  }
}