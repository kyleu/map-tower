package models

import play.api.db._
import play.api.Play.current

import org.scalaquery.session._
import org.scalaquery.ql.extended.PostgresDriver.Implicit._
import org.scalaquery.session._

object DatabaseBuilder {
  lazy val database = Database.forDataSource(DB.getDataSource())

  def wipe {
  }

  def create {
    database.withSession { implicit db: Session =>
      println((Tables.suppliers.ddl ++ Tables.users.ddl) create)
    }
  }

  def seed {

  }
}