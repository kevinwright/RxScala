import sbt._
import Keys._

object LocalKeys {
  val buildEnv        = TaskKey[Seq[File]]("buildenv")
  val buildEnvPackage = SettingKey[String]("buildenv-package")
  val buildEnvClass	  = SettingKey[String]("buildenv-class")
}

object BuildSettings {
  import LocalKeys._

  def mkValStr(name: String, value: String) =
    "  val " + name + " = \"" + value + "\""

  def uniqueBuildVersion: Option[String] = optEnv("GO_PIPELINE_COUNTER")

  def optEnv(key: String): Option[String] =
    Option(System.getenv get key)

  def env(key: String, default: String = ""): String =
    optEnv(key) getOrElse default

  def prop(key: String, default: String = ""): String =
    System.getProperties.getProperty(key, default)

  def mkBuildEnvClass(pkg: String, cls: String, entries: Map[String,String]): String = {
    (if (pkg.nonEmpty) "package " + pkg + "\n" else "") +
      "\n"+
      "trait " + cls + " {\n" +
      (entries map {case (k,v) => "  val " + k + ": String"} mkString "\n") + "\n" +
      "  def toMap: Map[String,String]" +
      "\n}\n\n" +
      "object " + cls + " extends " + cls + " {\n" +
      (entries map {case (k,v) => mkValStr(k,v)} mkString "\n") + "\n" +
      "  def toMap = Map(\n" +
      (entries map {case (k,v) => "    \"" + k + "\" -> " + k} mkString ",\n") + "\n" +
      "  )" +
      "\n}\n"
  }

  val buildSettings = Defaults.defaultSettings ++ Seq (
    shellPrompt  <<= (thisProjectRef, version) { (projref, ver) =>
      _ => projref.project +":"+ Git.branch +":"+ ver + ">"
    },
    buildEnvPackage	:= "rxscala",
    buildEnvClass	  := "BuildEnv",
    buildEnv <<= (Keys.sourceManaged, Keys.organization, Keys.name, Keys.version, buildEnvPackage, buildEnvClass) map {
      (sourceManaged: File, organization: String, name: String, version: String, buildEnvPackage: String, buildEnvClass: String)	=>
        val	file	= sourceManaged / "rxscala" / "BuildEnv.scala"
        val code	= mkBuildEnvClass(
          buildEnvPackage,
          buildEnvClass,
          Map(
            "organization" -> organization,
            "name" -> name,
            "version" -> version,
            "git_hash" -> Git.hash,
            "built_when" -> (new java.util.Date).toString,
            "build_machine" -> java.net.InetAddress.getLocalHost.getHostName,
            "build_vm" -> prop("java.version"),
            "build_number" -> optEnv("GO_PIPELINE_COUNTER").getOrElse("n/a")
          )
        )
        IO write (file, code)
        Seq(file)
    },
    sourceGenerators in Compile <+= buildEnv map identity
  )
}

object Git {
  object devnull extends ProcessLogger {
    def info (s: => String) {}
    def error (s: => String) {}
    def buffer[T] (f: => T): T = f
  }

  def silentExec(cmd: String) = cmd lines_! devnull

  def branch =
    silentExec("git status -sb").headOption getOrElse "-" stripPrefix "## "

  def hash =
    silentExec("git log --pretty=format:%H -n1").headOption getOrElse "-"
}
