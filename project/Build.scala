import sbt._
import Keys._

object Dependencies {
   val yuicompressor = "com.yahoo.platform.yui" % "yuicompressor" % "2.4.6"
   val less = "com.asual.lesscss" % "lesscss-engine" % "1.1.4"
   val scalaTest = "org.scalatest" %% "scalatest" % "1.6.1" % "test"
   val scalatra = "org.scalatra" %% "scalatra" % "2.1.0-SNAPSHOT"
   val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
   val rhino = "org.mozilla" % "rhino" % "1.7R3"

  val compileDeps = Seq(yuicompressor, less, rhino)
  val testDeps = Seq(scalaTest)
  val coreDeps = compileDeps ++ testDeps
  val scalatraDeps = coreDeps ++ Seq(scalatra, servlet)
}

object ScalatraBuild extends Build {
  import Dependencies._

  val buildScalaVersion = "2.9.1"
  val buildVersion      = "0.2"

  val buildSettings = Defaults.defaultSettings ++ Seq (
		  scalaVersion := buildScalaVersion,
		  version      := buildVersion,
		  resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
		  resolvers += "Asual.com" at "http://www.asual.com/maven/content/groups/public"
  )
    
  lazy val core = Project("core", file ("core"), settings = buildSettings ++ Seq (libraryDependencies := coreDeps))  
  lazy val scalatraLib = Project ("scalatra", file ("scalatra"), settings = buildSettings ++ Seq(libraryDependencies := scalatraDeps)) dependsOn (core)
}
