name := "zimzam"
organization := "com.github.jw3"
scalaVersion := "2.12.10"
scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-Ywarn-unused-import",
  "-Xfatal-warnings",
  "-Xlint:_"
)

val zioVersion = "1.0.0-RC18"
val rdf4jVersion = "3.1.1"
val scalatest = "3.0.3"
libraryDependencies := Seq(
  "dev.zio" %% "zio" % zioVersion,
  "org.clulab" %% "geonorm" % "0.9.7",
  "org.clulab" %% "timenorm" % "1.0.4",
  // ------------- test
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "org.scalactic" %% "scalactic" % scalatest % Test,
  "org.scalatest" %% "scalatest" % scalatest % Test
)

enablePlugins(GitVersioning, JavaServerAppPackaging)

val procVer = "73553234718da558664e7f7c3c17f6363e987170" // 03-16-2020
dependsOn(
  ProjectRef(uri(s"git://github.com/clulab/processors.git#$procVer"), "main"),
  ProjectRef(uri(s"git://github.com/clulab/processors.git#$procVer"), "corenlp"),
  ProjectRef(uri(s"git://github.com/clulab/processors.git#$procVer"), "odin")
)
