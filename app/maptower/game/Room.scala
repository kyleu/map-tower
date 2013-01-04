package maptower.game

import akka.actor._
import scala.concurrent.duration._

import play.api._
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import akka.util.Timeout
import akka.pattern.ask

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._

object Room {
  implicit val timeout = Timeout(1.seconds)

  lazy val default = {
    val roomActor = Akka.system.actorOf(Props[Room])
    // Create a bot user (just for fun)
    Robot(roomActor)
    roomActor
  }
  def join(username: String): scala.concurrent.Future[(Iteratee[String, _], Enumerator[String])] = {
    (default ? Join(username)).map {
      case Connected(enumerator) =>
        // Create an Iteratee to consume the feed
        val iteratee = Iteratee.foreach[String] { event =>
          default ! process(username, event)
        }.mapDone { _ =>
          default ! Quit(username)
        }
        (iteratee, enumerator)
      case CannotConnect(error) =>
        // Connection error
        // A finished Iteratee sending EOF
        val iteratee = Done[String, Unit]((), Input.EOF)
        // Send an error and close the socket
        val enumerator = Enumerator[String](error).andThen(Enumerator.enumInput(Input.EOF))
        (iteratee, enumerator)
    }
  }

  private def process(username: String, event: String) = {
    Talk(username, event)
  }
}

class Room extends Actor {
  var members = Map.empty[String, PushEnumerator[String]]

  def receive = {
    case Join(username) => {
      // Create an Enumerator to write to this socket
      val channel = Enumerator.imperative[String](onStart = () => self ! NotifyJoin(username))
      if (members.contains(username)) {
        sender ! CannotConnect("This username is already in use.")
      } else {
        members = members + (username -> channel)
        sender ! Connected(channel)
      }
    }

    case NotifyJoin(username) => {
      notifyAll("join", Some(username), "has joined the game.")
    }

    case Talk(username, text) => {
      notifyAll("talk", Some(username), text)
    }

    case msg: Spawn => {
      notifyAll("spawn", Some(msg.mob), msg)
    }

    case Quit(username) => {
      members = members - username
      notifyAll("quit", Some(username), "has left the game.")
    }
  }

  def notifyAll(kind: String, user: Option[String], data: Any) {
    val evt = new GameEvent(kind, user, data, Some(members.keySet.toList))
    val msg = evt.toString() //TODO generate(evt)
    println("Sending: " + msg)
    members.foreach {
      case (_, channel) => channel.push(msg)
    }
  }
}