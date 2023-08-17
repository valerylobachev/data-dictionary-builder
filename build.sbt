ThisBuild / version      := "0.3.1-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.11"

ThisBuild / organization         := "biz.lobachev.annette"
ThisBuild / organizationName     := "Valery Lobachev"
ThisBuild / organizationHomepage := Some(url("https://lobachev.biz/"))

ThisBuild / scmInfo    := Some(
  ScmInfo(
    url("https://github.com/valerylobachev/data-dictionary-builder"),
    "scm:git@github.com:valerylobachev/data-dictionary-builder.git",
  ),
)
ThisBuild / developers := List(
  Developer(
    id = "valerylobachev",
    name = "Valery Lobachev",
    email = "valery@lobachev.biz",
    url = url("https://lobachev.biz/"),
  ),
)

ThisBuild / description := "Annette Data Dictionary builder"
ThisBuild / licenses    := List("Apache-2.0" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage    := Some(url("https://github.com/valerylobachev/data-dictionary-builder"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo            := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle    := true

lazy val root = (project in file("."))
  .settings(
    name := "data-dictionary-builder",
  )

libraryDependencies += "org.scalatest"   %% "scalatest" % "3.2.15" % "test"
libraryDependencies += "com.google.guava" % "guava"     % "29.0-jre"

libraryDependencies += "org.scalatra.scalate" %% "scalate-core" % "1.9.8"

libraryDependencies += "org.apache.poi" % "poi"           % "5.2.3"
libraryDependencies += "org.apache.poi" % "poi-ooxml"     % "5.2.3"
libraryDependencies += "org.atteo"      % "evo-inflector" % "1.3"
