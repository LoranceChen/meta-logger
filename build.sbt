name := "MetaLogger"

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.11",
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-language:postfixOps"
  ),

  //macros
  scalacOptions += "-Xplugin-require:macroparadise",
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M8" cross CrossVersion.full)

)

lazy val root = project.in(file(".")).
  settings(commonSettings).
  settings(resolvers += Resolver.bintrayIvyRepo("scalameta", "maven")).
  settings(Seq(
    //    addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M8" cross CrossVersion.full),
    //    scalacOptions += "-Xplugin-require:macroparadise",
    scalacOptions in (Compile, console) := Seq(),
    updateOptions := updateOptions.value.withCachedResolution(true)
  )).
  settings(libraryDependencies ++= Seq(
    "org.scalameta" %% "scalameta" % "1.7.0" // % Provided // mark the dependency as % "provided" to exclude it from your runtime application.
  ))