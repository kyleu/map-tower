package controllers.forms

import play.api.data._
import play.api.data.Forms._
import maptower.map.Bounds

object Forms {
  val bounds = Form(
    mapping(
      "min" -> point,
      "max" -> point)(Bounds.apply)(Bounds.unapply))
}