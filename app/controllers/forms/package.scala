package controllers

import play.api.data._
import play.api.data.format.Formatter
import play.api.data.format.Formats.stringFormat
import play.api.data.Forms._
import maptower.map.Point

package object forms {
  def geoCoordFormat = new Formatter[Double] {
    override val format = Some("format.geocoord", Nil)

    def bind(key: String, data: Map[String, String]) = {
      stringFormat.bind(key, data).right.flatMap { s =>
        scala.util.control.Exception.allCatch[Double]
          .either(java.lang.Double.parseDouble(s))
          .left.map(e => Seq(FormError(key, "error.geocoord", Nil)))
      }
    }

    def unbind(key: String, value: Double) = Map(key -> value.toString)
  }

  val geocoord = FieldMapping[Double]()(geoCoordFormat)

  val point = mapping(
    "x" -> geocoord,
    "y" -> geocoord)(Point.apply)(Point.unapply)
}

