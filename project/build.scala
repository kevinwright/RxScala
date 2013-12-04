import sbt._
import Keys._
import sbtrelease.ReleasePlugin._
import net.virtualvoid.sbt.graph.Plugin.graphSettings

object Master extends Build {
  import BuildSettings._

  lazy val rxscala = Project(id = "rxscala", base = file("."))
    .configs(IntegrationTest)
    .settings(allSettings : _*)

  lazy val allSettings =
    graphSettings ++
    releaseSettings ++
    //deploySettings ++
    Defaults.itSettings ++
    Seq(
      ivyXML := ivyDeps
    )

  val ivyDeps = {
    <dependencies>
      <!-- commons logging is evil. It does bad, bad things to the classpath and must die. We use slf4j instead -->
        <exclude module="commons-logging"/>
      <!-- Akka dependencies must be excluded if transitivly included,
           replaced with corresponding atmos-akka-xxx -->
      
        <!--
        <exclude module="akka-actor"/>
        <exclude module="akka-remote"/>
        <exclude module="akka-slf4j"/>
        <exclude module="slf4j-simple"/>
        -->
      
      <!-- Flume dependencies can be excluded if Flume isn't used -->
        <exclude module="flume"/>
        <exclude module="googleColl"/>
        <exclude module="libthrift"/>
        <exclude module="hadoop-core"/>
    </dependencies>
  }
}
