package maptower.game

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.duration.intToDurationInt
import akka.util.Timeout
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.iteratee.Iteratee
import play.api.Logger

object Robot {
  def apply(gameRoom: ActorRef) {
    val loggerIteratee = Iteratee.foreach[String](event => Logger("robot").info(event))

    implicit val timeout = Timeout(1 second)

    gameRoom ? (Join("Robot")) map {
      case Connected(robotChannel) =>
        // Apply this Enumerator on the logger.
        robotChannel |>> loggerIteratee
    }

    // Make the robot talk every 30 seconds
    Akka.system.scheduler.schedule(
      30 seconds,
      30 seconds,
      gameRoom,
      Talk("Robot", "I'm still alive")
    )

    // Make the robot spawn every 5 seconds
    Akka.system.scheduler.schedule(
      5 seconds,
      5 seconds,
      gameRoom,
      Spawn("RobotMob", 0.0, 0.0)
    )
  }
}