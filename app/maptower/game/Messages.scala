package maptower.game

import play.api.libs.iteratee.Enumerator
import play.api.libs.json.JsValue

case class GameEvent(kind: String, user: Option[String], data: JsValue, members: Option[List[String]])

case class Join(username: String)
case class Quit(username: String)
case class Talk(username: String, text: String)
case class NotifyJoin(username: String)

case class Spawn(mob: String, x: Double, y: Double)

case class Connected(enumerator: Enumerator[String])
case class CannotConnect(msg: String)
