//import AssemblyKeys._ 
//import sbtassembly._


//assemblySettings


name := "Simple Project"

version := "1.0"

scalaVersion := "2.10.5"

     
libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.0" % "provided"

libraryDependencies += "org.apache.spark" %% "spark-mllib" % "1.6.0"

libraryDependencies += "org.apache.spark" %% "spark-hive" % "1.1.0"

libraryDependencies += "org.apache.hbase" % "hbase-client" % "1.2.0" % "provided"
libraryDependencies += "org.apache.hbase" % "hbase-common" % "1.2.0" % "provided"
libraryDependencies += "org.apache.hbase" % "hbase-server" % "1.2.0" % "provided"


libraryDependencies += "org.apache.spark" %% "spark-streaming" % "1.6.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka" % "1.6.0"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.0.8"


addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.12.0")

//mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>  
  //{  
    //case PathList("org", "slf4j", xs @ _*)         => MergeStrategy.first  
    //case PathList(ps @ _*) if ps.last endsWith "axiom.xml" => MergeStrategy.filterDistinctLines  
    //case PathList(ps @ _*) if ps.last endsWith "Log$Logger.class" => MergeStrategy.first  
    //case PathList(ps @ _*) if ps.last endsWith "ILoggerFactory.class" => MergeStrategy.first  
    //case x => old(x)  
  //}  
//}


//excludedFiles in assembly <<=
//(excludedFiles in assembly) {
  //(old) => (bases) =>
    //old(bases) ++ (bases flatMap (base =>
      //(base / "META-INF/ECLIPSEF.RSA").get))
//}

//assemblyMergeStrategy in assembly := {
  //case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  //case "application.conf"            => MergeStrategy.concat
  //case "reference.conf"              => MergeStrategy.concat
  //case x =>
    //val baseStrategy = (assemblyMergeStrategy in assembly).value
    //baseStrategy(x)
//}


assemblyMergeStrategy in assembly := {
 case PathList("META-INF", xs @ _*) => MergeStrategy.discard
 case x => MergeStrategy.first
}
 
