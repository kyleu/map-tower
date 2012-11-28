import sbt._
import Keys._
import PlayProject._

trait BuildHooks {

}

object ApplicationBuild extends Build {
  val appName = "MapTower"
  val appVersion = "1.0"

  val appDependencies = Seq(
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "org.scalaquery" % "scalaquery_2.9.0-1" % "0.9.5",
    "org.mongodb" % "casbah_2.9.1" % "2.4.0",
    "org.scala-tools.testing" %% "scalacheck" % "1.9",
    "org.json4s" %% "json4s-native" % "3.0.0"
  )

  println("Reloadinating!")

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings()
}
