package controllers.forms

import play.api.data._
import play.api.data.Forms._
import map.Bounds

object Forms {
  val bounds = Form(
    mapping(
      "ul" -> point,
      "br" -> point)(Bounds.apply)(Bounds.unapply))
}