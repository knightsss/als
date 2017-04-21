import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import scala.math._

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.mapreduce.TableInputFormat

//import org.apache.hadoop.mapreduce.Job  
//import org.apache.hadoop.hbase.io.ImmutableBytesWritable  
//import org.apache.hadoop.hbase.client.Result  
//import org.apache.hadoop.hbase.client.Put  
//import org.apache.hadoop.hbase.util.Bytes
 
object OperatorHbase {
    def main(args: Array[String]) {

val spark_conf = new SparkConf().setAppName("Hbase Application")
val conf = HBaseConfiguration.create()
val sc = new SparkContext(spark_conf)

//val tablename = "student"        
//sc.hadoopConfiguration.set(TableOutputFormat.OUTPUT_TABLE, tablename)  
conf.set(TableInputFormat.INPUT_TABLE, "test1")
    val stuRDD = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat],
  classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
  classOf[org.apache.hadoop.hbase.client.Result])
    val count = stuRDD
    count.collect.foreach(println)
    //println("Students RDD Count:" + count)
    //stuRDD.cache()

}
}
