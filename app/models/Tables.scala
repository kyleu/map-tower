package models

import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.basic.{ BasicTable => Table }
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql._
import org.scalaquery.session._

object Tables {
  val users = new Table[(Int, String, Option[String])]("users") {
    def id = column[Int]("id", O NotNull)
    def first = column[String]("first", O Default "NFN", O DBType "varchar(64)")
    def last = column[Option[String]]("last")
    def * = id ~ first ~ last
  }

  val suppliers = new Table[(Int, String, String, String, String, String)]("SUPPLIERS") {
    def id = column[Int]("SUP_ID", O.PrimaryKey) // This is the primary key column
    def name = column[String]("SUP_NAME")
    def street = column[String]("STREET")
    def city = column[String]("CITY")
    def state = column[String]("STATE")
    def zip = column[String]("ZIP")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = id ~ name ~ street ~ city ~ state ~ zip
  }
}
