package controllers

import scala.io.Source
import java.util.Date
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.gridfs.Imports._
import map.{ Point, Bounds, Line }
import play.api._
import play.api.mvc._
import java.io.File
import com.codahale.jerkson.Json._
import play.api.libs.iteratee.Enumerator

object Tiles extends Controller {
  def get(z: Int, x: Int, y: Int) = Action {
    val file = TileCache.get(z, y, x)
    Ok.sendFile(file, true).as("image/png")
  }
}

object TileCache {
  import java.io._

  private val styleId = 998
  private val rootUrl = "http://%s.tile.cloudmade.com/0320d0049e1a4242bab7857cec8b343a/%s/256/".format("a", styleId)

  private val mongoDb = MongoConnection()("maptower")
  private val mongoFs = GridFS(MongoConnection()("tiles"))

  private val cacheDir = new File("./data/cache")

  def get(z: Int, x: Int, y: Int) = {
    val filename = "%s/%s/%s.png".format(z, x, y)
    val file = new File(cacheDir, filename)
    if (!file.exists) {
      file.createNewFile()
      cache(file)
    }
    file
  }

  private def cache(file: File) = {
    val fos = new java.io.BufferedOutputStream(new java.io.FileOutputStream(file))
    val uc = new java.net.URL(rootUrl + file.getName).openConnection();
    val contentType = uc.getContentType();
    val contentLength = uc.getContentLength();
    if (contentType.startsWith("text/") || contentLength == -1) {
      throw new java.io.IOException("This is not a binary file.");
    }
    val input = new BufferedInputStream(uc.getInputStream());
    Iterator.continually(input.read)
      .takeWhile(-1 !=)
      .foreach(fos.write)

    input.close()
    fos.close()

    file
  }
}
