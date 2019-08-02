name := """git-info"""
organization := "com.maocq"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"
scalacOptions += "-Ypartial-unification"

libraryDependencies += guice
libraryDependencies ++= Seq(
  ws,
  evolutions,
  "com.typesafe.play"               %% "play-slick"              % "3.0.0",
  "com.typesafe.play"               %% "play-slick-evolutions"   % "3.0.0",
  "org.postgresql"                  % "postgresql"               % "42.1.4",
  "org.typelevel"                   %% "cats-core"               % "2.0.0-M4",
  "io.monix"                        %% "monix"                   % "3.0.0-RC3",
  "org.scalatestplus.play"          %% "scalatestplus-play"      % "4.0.3" % Test
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.maocq.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.maocq.binders._"
