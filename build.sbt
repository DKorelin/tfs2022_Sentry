ThisBuild / scalaVersion := "2.13.8"

lazy val circeVersion = "0.14.1"
lazy val AkkaVersion = "2.6.19"
lazy val doobieVersion = "1.0.0-RC1"
lazy val http4sVersion = "1.0.0-M23"

ThisBuild / libraryDependencies ++= Seq(
  "com.softwaremill.sttp.client3" %% "core" % "3.5.2",
  "com.softwaremill.sttp.client3" %% "httpclient-backend" % "3.5.2",
  "de.heikoseeberger" %% "akka-http-circe" % "1.39.2",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.softwaremill.macwire" %% "macros" % "2.5.7",
  "com.beachape" %% "enumeratum" % "1.7.0",
  "com.beachape" %% "enumeratum-circe" % "1.7.0",
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.18" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % "10.2.8" % Test,
  "org.scalamock" %% "scalamock" % "4.4.0" % Test,
  "org.scalatest" %% "scalatest" % "3.2.2" % Test,
  "com.h2database"  %  "h2"                % "2.1.210",
  "org.tpolecat" %% "doobie-core"      % doobieVersion,
  "org.tpolecat" %% "doobie-h2"        % doobieVersion,
  "org.scalamock" %% "scalamock" % "4.4.0" % Test,
  "org.scalatest" %% "scalatest" % "3.2.2" % Test
)
