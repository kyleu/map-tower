package maptower.game

import akka.actor._
import akka.util.duration._

import play.api._
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import com.codahale.jerkson.Json.generate

import akka.util.Timeout
import akka.pattern.ask

import play.api.Play.current

object Room {
  implicit val timeout = Timeout(1 second)

  lazy val default = {
    val roomActor = Akka.system.actorOf(Props[Room])
    // Create a bot user (just for fun)
    Robot(roomActor)
    roomActor
  }

  def join(username: String): Promise[(Iteratee[String, _], Enumerator[String])] = {
    (default ? Join(username)).asPromise.map {
      case Connected(enumerator) =>
        // Create an Iteratee to consume the feed
        val iteratee = Iteratee.foreach[String] { event =>
          default ! Talk(username, event)
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
}

class Room extends Actor {
  var members = Map.empty[String, PushEnumerator[String]]

  def receive = {
    case Join(username) => {
      // Create an Enumerator to write to this socket
      val channel = Enumerator.imperative[String](onStart = self ! NotifyJoin(username))
      if (members.contains(username)) {
        sender ! CannotConnect("This username is already in use.")
      } else {
        members = members + (username -> channel)
        sender ! Connected(channel)
      }
    }

    case NotifyJoin(username) => {
      notifyAll("join", username, "has joined the game.")
    }

    case Talk(username, text) => {
      notifyAll("talk", username, text)
    }

    case Spawn(mob: String, x: Double, y: Double) => {
      notifyAll("talk", mob, x + "/" + y)
    }

    case Quit(username) => {
      members = members - username
      notifyAll("quit", username, "has left the game.")
    }
  }

  def notifyAll(kind: String, user: String, text: String) {
    val msg = generate(Map(
      "kind" -> kind,
      "user" -> user,
      "message" -> text,
      "members" -> members.keySet.toList
    ))
    members.foreach {
      case (_, channel) => channel.push(msg)
    }
  }
}