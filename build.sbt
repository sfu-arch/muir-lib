// name := "dandelion-lib"

import sbt.complete._
import sbt.complete.DefaultParsers._
import xerial.sbt.pack._
import sys.process._

enablePlugins(PackPlugin)

lazy val commonSettings = Seq(
  name := "dandelion-lib",
  organization := "edu.sfu.cs",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.12.10",
  Global / parallelExecution := true,
  Test / parallelExecution := true,
  Test / logBuffered := false,
  Test / testOptions += Tests.Argument("-oDF"),
  Test / traceLevel := 15,
  scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xsource:2.11"),
  addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % "3.5.0" cross CrossVersion.full),
  libraryDependencies ++= Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value),
  libraryDependencies ++= Seq("org.json4s" %% "json4s-jackson" % "3.6.1"),
  libraryDependencies ++= Seq("com.lihaoyi" %% "sourcecode" % "0.1.4"),
  libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.0.1"),
  libraryDependencies ++= Seq("org.scalacheck" %% "scalacheck" % "1.13.4"),
  libraryDependencies ++= Seq("edu.berkeley.cs" %% "chisel-iotesters" % "1.3-SNAPSHOT"),
  libraryDependencies ++= Seq("edu.berkeley.cs" %% "dsptools" % "1.5.0"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    Resolver.mavenLocal
  )
)

// lazy val chisel = (project in file("chisel3")).settings(commonSettings)

def dependOnChisel(prj: Project) = {
  prj.settings(
    libraryDependencies ++= Seq("edu.berkeley.cs" %% "chisel3" % "3.5.0")
  )
}


lazy val `api-config-chipsalliance` = (project in file("api-config-chipsalliance/build-rules/sbt"))
  .settings(commonSettings)
  .settings(publishArtifact := false)
lazy val hardfloat = dependOnChisel(project)
  .settings(commonSettings)
  .settings(publishArtifact := false)

lazy val dandelion = dependOnChisel(project in file("."))
  .settings(commonSettings, chipSettings)
  .dependsOn(`api-config-chipsalliance` % "compile-internal;test-internal")
  .dependsOn(hardfloat % "compile-internal;test-internal")
  .settings(
    aggregate := false,
    // Include macro classes, resources, and sources in main jar.
    Compile / packageSrc / mappings ++= (mappings in (`api-config-chipsalliance`, Compile, packageBin)).value,
    Compile / packageSrc / mappings ++= (mappings in (`api-config-chipsalliance`, Compile, packageSrc)).value,
    Compile / packageSrc / mappings ++= (mappings in (hardfloat, Compile, packageBin)).value,
    Compile / packageSrc / mappings ++= (mappings in (hardfloat, Compile, packageSrc)).value,
    exportJars := true
  )

lazy val addons = settingKey[Seq[String]]("list of addons used for this build")
lazy val make = inputKey[Unit]("trigger backend-specific makefile command")
val setMake = NotSpace ~ (Space ~> NotSpace)

lazy val chipSettings = Seq(
  addons := {
    val a = sys.env.getOrElse("DANDELION_ADDONS", "")
    println(s"Using addons: $a")
    a.split(" ")
  },
  unmanagedSourceDirectories in Compile ++= addons.value.map(baseDirectory.value / _ / "src/main/scala"),
  mainClass in(Compile, run) := Some("dandelion.Generator"),
  make := {
    val jobs = java.lang.Runtime.getRuntime.availableProcessors
    val (makeDir, target) = setMake.parsed
    (run in Compile).evaluated
    s"make -C $makeDir  -j $jobs $target".!
  }
)

