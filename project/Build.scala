import sbt._
import Keys._
import PlayProject._

trait BuildHooks {

}

object ApplicationBuild extends Build {
  val appName = "MapTower"
  val appVersion = "1.0"

  val appDependencies = Seq(
    //"org.mongodb" % "casbah_2.10" % "2.4.0"
    "com.github.tmingos" % "casbah_2.10" % "2.5.0-SNAPSHOT"
  )

  println("Reloadinating!")

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
    //scalacOptions += "-usejavacp"
  )
}
