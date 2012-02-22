package controllers

import play.api._
import play.api.mvc._
import com.codahale.jerkson.Json._
import com.gravity.goose.{ Configuration, Goose }

object Newsletter extends Controller {
  def index() = Action {
    Ok(views.html.newsletter())
  }

  def add(contentType: String, url: String) = Action {
    contentType match {
      case "url" =>
        val configuration = new Configuration()
        //configuration.setLocalStoragePath("/tmp/goose")
        //configuration.setEnableImageFetching(false)
        configuration.setImagemagickConvertPath("/Users/kyle/Projects/Libraries/ImageMagick-6.7.5/bin")
        val goose = new Goose(configuration)
        val article = goose.extractContent(url)
        Ok(views.html.article(article))
    }
  }
}
