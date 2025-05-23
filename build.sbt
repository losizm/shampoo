organization  := "com.github.losizm"
name          := "shampoo"
version       := "1.0.0"
versionScheme := Some("early-semver")
description   := "The YAML library for Scala"
homepage      := Some(url("https://github.com/losizm/shampoo"))
licenses      := List("Apache License, Version 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

scalaVersion  := "3.3.5"
scalacOptions := Seq("-deprecation", "-feature", "-new-syntax", "-Werror", "-Yno-experimental")

Compile / doc / scalacOptions := Seq(
  "-project", name.value.capitalize,
  "-project-version", version.value,
  "-project-logo", "images/logo.svg"
)

libraryDependencies ++= Seq(
  "org.snakeyaml" %  "snakeyaml-engine" % "2.9"    % Compile,
  "org.scalatest" %% "scalatest"        % "3.2.19" % Test
)

developers := List(
  Developer(
    id    = "losizm",
    name  = "Carlos Conyers",
    email = "carlos.conyers@hotmail.com",
    url   = url("https://github.com/losizm")
  )
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/losizm/shampoo"),
    "scm:git@github.com:losizm/shampoo.git"
  )
)

publishMavenStyle := true

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org"

  isSnapshot.value match {
    case true  => Some("snaphsots" at s"$nexus/content/repositories/snapshots")
    case false => Some("releases"  at s"$nexus/service/local/staging/deploy/maven2")
  }
}
