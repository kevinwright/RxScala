import sbtrelease._
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys._
import sbtrelease.ReleaseStateTransformations._
import BuildSettings.{optEnv,uniqueBuildVersion,prop}
import sbt.Package.ManifestAttributes

name := "rxscala"

organization := "net.thecoda"

scalaVersion := "2.10.3"

scalacOptions := Seq("-feature", "-deprecation", "-unchecked", "-Xlint", "-encoding", "utf8")

parallelExecution in Test := true

resolvers ++= Seq(
  "Maven Central"          at "http://repo1.maven.org/maven2/",
  "Typesafe Repository"    at "http://repo.typesafe.com/typesafe/releases/",
  "Atmos Repo"             at "http://repo.typesafe.com/typesafe/atmos-releases/",
  "Sonatype OSS Releases"  at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Seq(
//Reflection (for macros)
  "org.scala-lang"              %  "scala-reflect"                 % "2.10.3",
//Rx
  "com.netflix.rxjava"          %  "rxjava-core"                   % "0.15.1",
//
// Akka
//
  "com.typesafe.akka"           %% "akka-actor"                    % "2.2.3",
  "com.typesafe.akka"           %% "akka-slf4j"                    % "2.2.3",
  "com.typesafe.akka"           %% "akka-agent"                    % "2.2.3",
  "com.typesafe.akka"           %% "akka-testkit"                  % "2.2.3"               % "it,test",
//
// Logging
//
  "org.slf4j"                   %  "slf4j-api"                     % "1.7.5",
  "org.slf4j"                   %  "jcl-over-slf4j"                % "1.7.5",
  "ch.qos.logback"              %  "logback-classic"               % "1.0.0",
  "org.codehaus.groovy"         %  "groovy-all"                    % "1.7.6",
  "janino"                      %  "janino"                        % "2.5.10",
//
// test and local debug:
//
  "org.scalatest"               %% "scalatest"                     % "2.0"               % "it,test"
)

credentials += Credentials(Path.userHome / ".ivy2" / "sonatype.credentials")

credentials += Credentials(Path.userHome / ".ivy2" / "atmos.credentials")

publishMavenStyle := true

packageOptions <<= (Keys.version, Keys.name, Keys.artifact) map {
  (version: String, name: String, artifact: Artifact) =>
    Seq(ManifestAttributes(
      "Implementation-Vendor" -> "thecoda.net",
      "Implementation-Title" -> "rxscala",
      "Version" -> version,
      "Build-Number" -> optEnv("GO_PIPELINE_COUNTER").getOrElse("n/a"),
      "Group-Id" -> name,
      "Artifact-Id" -> artifact.name,
      "Git-SHA1" -> Git.hash,
      "Git-Branch" -> Git.branch,
      "Built-By" -> "Oompa-Loompas",
      "Build-Jdk" -> prop("java.version"),
      "Built-When" -> (new java.util.Date).toString,
      "Build-Machine" -> java.net.InetAddress.getLocalHost.getHostName
    )
  )
}

testOptions in Test += Tests.Argument("html", "console", "junitxml")



