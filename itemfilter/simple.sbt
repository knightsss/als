name := "Simple Project"

version := "1.0"

scalaVersion := "2.10.5"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.0"

libraryDependencies += "org.apache.spark" %% "spark-mllib" % "1.6.0"

libraryDependencies += "org.apache.spark" %% "spark-hive" % "1.1.0"

libraryDependencies += "org.apache.hbase" % "hbase-client" % "1.2.0"
libraryDependencies += "org.apache.hbase" % "hbase-common" % "1.2.0"
libraryDependencies += "org.apache.hbase" % "hbase-server" % "1.2.0"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % "1.6.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka" % "1.6.0"
