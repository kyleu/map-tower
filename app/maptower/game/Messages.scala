package maptower.game

import play.api.libs.iteratee.Enumerator

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class GameEvent(kind: String, user: Option[String], data: Any, members: Option[List[String]])

object Foo {
  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  case class Person(name: String, age: Int, lovesChocolate: Boolean)

  implicit val personReads = Json.reads[Person]

  //implicit val readsGameEvent = Json.reads[GameEvent]
  //implicit val writesGameEvent = Json.writes[GameEvent]
}

case class Join(username: String)
case class Quit(username: String)
case class Talk(username: String, text: String)
case class NotifyJoin(username: String)

case class Spawn(mob: String, x: Double, y: Double)

case class Connected(enumerator: Enumerator[String])
case class CannotConnect(msg: String)
