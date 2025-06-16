ThisBuild / version      := "0.4.10-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.16"

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
ThisBuild / licenses    := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage    := Some(url("https://github.com/valerylobachev/data-dictionary-builder"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle    := true
ThisBuild / publishTo            := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if (isSnapshot.value) Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}

lazy val root = (project in file("."))
  .settings(
    name := "data-dictionary-builder",
  )

libraryDependencies += "org.scalatest"   %% "scalatest" % "3.2.19" % "test"
libraryDependencies += "com.google.guava" % "guava"     % "33.4.8-jre"

libraryDependencies += "org.scalatra.scalate" %% "scalate-core" % "1.10.1"

libraryDependencies += "org.apache.poi" % "poi"           % "5.4.1"
libraryDependencies += "org.apache.poi" % "poi-ooxml"     % "5.4.1"
libraryDependencies += "org.atteo"      % "evo-inflector" % "1.3"

val circeVersion = "0.14.14"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core"    % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser"  % circeVersion,
)
