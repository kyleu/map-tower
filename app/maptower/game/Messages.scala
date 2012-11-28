package maptower.game

import play.api.libs.iteratee.Enumerator

case class Join(username: String)
case class Quit(username: String)
case class Talk(username: String, text: String)
case class Spawn(mob: String, x: Double, y: Double)
case class NotifyJoin(username: String)

case class Connected(enumerator: Enumerator[String])
case class CannotConnect(msg: String)