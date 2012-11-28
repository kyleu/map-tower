package maptower.game

import play.api.libs.iteratee.Enumerator
import play.api.libs.json.JsValue

case class Join(username: String)
case class Quit(username: String)
case class Talk(username: String, text: String)
case class Spawn(mob: String, x: Double, y: Double)
case class NotifyJoin(username: String)

case class Connected(enumerator: Enumerator[JsValue])
case class CannotConnect(msg: String)
