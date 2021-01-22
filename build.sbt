
lazy val allScalaVersion = "3.0.0-M3"

ThisBuild / scalaVersion := allScalaVersion

lazy val root = project.in(file(".")).
  aggregate(subtreeSetting, subtreeAppending).
  settings(
    publish := {},
    publishLocal := {},
  )

lazy val commonSettings = Seq(
  version := "0.1.0",
  scalaVersion := allScalaVersion,
  scalaJSUseMainModuleInitializer := true,
  libraryDependencies ++= Seq(
    "org.scala-js" % "scalajs-dom_sjs1_2.13" % "1.1.0",
    "com.lihaoyi" % "scalatags_sjs1_2.13" % "0.9.2",
    "com.ellbur" % "quicksignals_sjs1_3.0.0-M3" % "0.2.0",
  )
)

lazy val subtreeSetting = project
  .enablePlugins(ScalaJSPlugin)
  .in(file("./demos/subtree-setting"))
  .settings(commonSettings)
  .settings(
    name := "quicksignals-js-demos-subtree-setting",
    scalaSource in Compile := baseDirectory.value / "src",
  )

lazy val subtreeAppending = project
  .enablePlugins(ScalaJSPlugin)
  .in(file("./demos/subtree-appending"))
  .settings(commonSettings)
  .settings(
    name := "quicksignals-js-demos-subtree-appending",
    scalaSource in Compile := baseDirectory.value / "src",
  )

