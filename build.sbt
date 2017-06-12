version := "0.1"

name := "meta-logger"

organization := "com.scalachan"

scalaVersion := "2.12.2"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

overridePublishSettings

sonatypeProfileName := "com.scalachan"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-Xplugin-require:macroparadise"
)

enablePlugins(SignedAetherPlugin)
//
disablePlugins(AetherPlugin)


pgpSecretRing := file(Path.userHome + "/.sbt/gpg/secring.asc")

pgpPublicRing := file(Path.userHome + "/.sbt/gpg/pubring.asc")

publishArtifact in (Compile, packageDoc) := true

resolvers ++= Seq(
  "main" at "http://repo1.maven.org/maven2",

  //  "Sonatype Nexus" at "http://localhost:7070/nexus/repository/maven-releases/"//"https://oss.sonatype.org/content/repositories/snapshots"
  "Sonatype Nexus" at "https://oss.sonatype.org/content/repositories/snapshots"
)

resolvers += Resolver.bintrayIvyRepo("scalameta", "maven")

parallelExecution in Test := false

//overridePublishSettings
//
//sonatypeProfileName := "com.scalachan"


//macros
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M9" cross CrossVersion.full)

scalacOptions in (Compile, console) := Seq()
updateOptions := updateOptions.value.withCachedResolution(true)
libraryDependencies ++= Seq(
  "org.scalameta" %% "scalameta" % "1.8.0" // % Provided // mark the dependency as % "provided" to exclude it from your runtime application.
)

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  //  Some("Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
  else
    Some("Sonatype Nexus Staging"  at nexus + "service/local/staging/deploy/maven2")
  //    Some("releases"  at "http://localhost:7070/nexus/repository/maven-releases/")

}

publishArtifact in Test := false

credentials += Credentials(Path.userHome / ".ivy2" / ".nexus_cred")

pomExtra in Global :=
  <url>https://github.com/LoranceChen/meta-logger</url>
    <licenses>
      <license>
        <name>Apache License, Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0</url>
      </license>
    </licenses>
    <scm>
      <url>git@github.com/LoranceChen/meta-logger.git</url>
      <connection>scm:git:git@github.com/LoranceChen/meta-logger.git</connection>
    </scm>
    <developers>
      <developer>
        <id>lorancechen</id>
        <name>UnlimitedCode Inc.</name>
        <url>http://www.scalachan.com/</url>
      </developer>
    </developers>