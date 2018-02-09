name := "Ru translit metaphone"

version := "0.1.0"

scalaVersion := "2.11.6"

libraryDependencies += "org.specs2" %% "specs2-core" % "4.0.2" % "test"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.2.1"

scalacOptions in Test ++= Seq("-Yrangepos")
