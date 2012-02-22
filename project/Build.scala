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
    "com.mongodb.casbah" %% "casbah" % "2.1.5-1",
    "org.scala-tools.testing" %% "scalacheck" % "1.9",

    //goose crap
    "org.jsoup" % "jsoup" % "1.5.2",
    "commons-io" % "commons-io" % "2.0.1",
    "org.apache.httpcomponents" % "httpclient" % "4.1.2",
    "goose" % "goose" % "2.1.11" from "file://Users/kyle/Projects/Libraries/goose/target/goose-2.1.11.jar"
  )

  println("Reloadinating!")

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings()
}
