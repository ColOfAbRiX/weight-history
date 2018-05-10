name := "Weight History"

version := "1.0"

lazy val weighthistory = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  jdbc, ehcache, ws, guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % "test",
  "org.scalaz" %% "scalaz-core" % "7.2.22",
  "org.scalaz" %% "scalaz-effect" % "7.2.22",
  "org.playframework.anorm" %% "anorm" % "2.6.2",
  "org.xerial" % "sqlite-jdbc" % "3.21.0.1"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
