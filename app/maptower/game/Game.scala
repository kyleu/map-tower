package maptower.game

object Game {
  private val games = scala.collection.mutable.Map[String, Game]()

  def find(id: String) = {
    games.get(id) match {
      case Some(game) => game
      case None => {
        val game = new Game(id)
        games(id) = game
        game
      }
    }
  }
}

case class Game(id: String) {

}