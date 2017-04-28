import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object SimpleApp {
    def main(args: Array[String]) {
        val logFile = "/user/root/data.txt"; // Should be some file on your system
        val conf = new SparkConf().setAppName("Simple Application");
        val sc = new SparkContext(conf);
        val logData = sc.textFile(logFile, 2).cache();
        //val textFile = sc.textFile("/user/root/data.txt");
        val numAs = logData.count();
        println(numAs);
    }
}
