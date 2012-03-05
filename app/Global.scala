import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown")
  }
}