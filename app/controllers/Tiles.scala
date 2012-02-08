package controllers

import scala.io.Source
import java.util.Date
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.gridfs.Imports._
import map.{ Point, Bounds, Line }
import play.api._
import play.api.mvc._
import play.libs.Akka
import java.io.File
import com.codahale.jerkson.Json._
import play.api.libs.iteratee.Enumerator

object Tiles extends Controller {
  def get(z: Int, x: Int, y: Int) = Action {
    val file = TileCache.get(z, y, x)
    Ok.stream(Enumerator.fromStream(file.inputStream)).as("image/png").withHeaders(CACHE_CONTROL -> "max-age=3600", EXPIRES -> "Tue, 14 Feb 2012 14:19:41 GMT")
  }
}

object TileCache {
  import java.io._

  private val styleId = 998
  private val rootUrl = "http://%s.tile.cloudmade.com/0320d0049e1a4242bab7857cec8b343a/%s/256/".format("a", styleId)

  private lazy val mongoDb = MongoConnection()("maptower")
  private lazy val mongoFs = GridFS(mongoDb)

  def get(z: Int, x: Int, y: Int): GridFSDBFile = {
    val filename = "%s-%s-%s.png".format(z, y, x)
    mongoFs.findOne(filename) getOrElse {
      cache(filename)
      mongoFs.findOne(filename).get
    }
  }

  private def cache(filename: String) = {
    val url = rootUrl + filename.replace("-", "/")
    val uc = new java.net.URL(url).openConnection();
    val contentType = uc.getContentType();
    val contentLength = uc.getContentLength();
    if (contentType.startsWith("text/") || contentLength == -1) {
      throw new java.io.IOException("This is not a binary file.");
    }

    val input = new BufferedInputStream(uc.getInputStream());

    mongoFs(input) { fh =>
      fh.filename = filename
      fh.contentType = contentType
    }
    Logger.info("Cached %s as %s in %sms.".format(filename, contentType, 0))
  }
}
