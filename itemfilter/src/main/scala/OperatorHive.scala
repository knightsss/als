import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import scala.math._
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext

object OperatorHive {
    def main(args: Array[String]) {

        val conf = new SparkConf().setAppName("Als Application")
        val sc = new SparkContext(conf)
        val sqlContext = new org.apache.spark.sql.hive.HiveContext(sc)

        val studentRDD = sqlContext.sql("select * from db_test.tez_test1").rdd

        studentRDD.collect().foreach(println)
}
}
