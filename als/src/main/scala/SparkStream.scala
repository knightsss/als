import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import scala.math._

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Duration, StreamingContext}


import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import org.apache.spark.mllib.recommendation.Rating

import java.sql.DriverManager
import java.sql.Connection

import java.util.Date


object SparkStream {
    def main(args: Array[String]) {

val conf = new SparkConf().setAppName("spark streaming")
val sc = new SparkContext(conf)

val scc = new StreamingContext(sc, Duration(5000))
scc.sparkContext.setLogLevel("ERROR")
scc.checkpoint(".") // 因为使用到了updateStateByKey,所以必须要设置checkpoint
val topics = Set("als_test") //我们需要消费的kafka数据的topic
val brokers = "192.168.8.206:9092,192.168.8.207:9092,192.168.8.208:9092"
def createStream(scc: StreamingContext, kafkaParam: Map[String, String], topics: Set[String]) = {
    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](scc, kafkaParam, topics)
  }
val kafkaParam = Map[String, String](
      "metadata.broker.list" -> brokers,// kafka的broker list地址
      "serializer.class" -> "kafka.serializer.StringEncoder"
    )




//var conn = new ScalaJdbcConnect()

//conn.loadDataToMysql

val stream: InputDStream[(String, String)] = createStream(scc, kafkaParam, topics)

val sameModel = MatrixFactorizationModel.load(sc, "/user/root/targetmodel")

val test_data = stream.map(_._2).map(_.split(' ') match { case Array(user, item) => (user,item)
})

//test_data.foreachRDD(rdd => {rdd.foreach {
//record => println(record)
//}})

//base_time = 0

test_data.foreachRDD{ rdd =>
val x= rdd.count
if (x>0){
println(x)
var conn = new ScalaJdbcConnect()
println("connect")
for(line <- rdd.collect.toArray){

//var base_time = 0

var user = line._1.toInt
println(user)
var item = line._2.toInt
var score = line._2.toFloat

//conn.insertDataMysql(user,item,score)

//println(item)
//println(sameModel.predict(user,item))



//println("the ten items like")

var like_items_ratings = sameModel.recommendProducts(user,10)

//println(sameModel.recommendProducts(user,10).mkString("\n"))
for (rat <- like_items_ratings){
//println(rat.user,rat.product,rat.rating)
conn.insertDataMysql(rat.user.toString,rat.product.toString,rat.rating.toFloat)
//conn.loadDataToMysql
}

var now = new Date()
var a = now.getTime
println(a)
//var base_time = a

}
}
}


//test_data.print()
//val predict_data =  sameModel.predict(196,242)
//println(predict_data)
scc.start() // 真正启动程序
scc.awaitTermination() //阻塞等待

}}











class ScalaJdbcConnect{
  val driver = "com.mysql.jdbc.Driver"
  val url = "jdbc:mysql://192.168.8.207:3306/user_item"
  val username = "als"
  val password = "als"

  var connection:Connection = null

  Class.forName(driver)
  connection = DriverManager.getConnection(url, username, password)

  val statement = connection.createStatement()

  def insertDataMysql(user:String, item:String, score:Float): Unit ={

    var sql =  "insert into user_item_score values('"+user+"','"+item+"','"+score+"')"
    statement.executeUpdate(sql)
    //println("insert into mysql finish!")

  }
  def closeMysql: Unit ={
    statement.close()
    connection.close()
  }

  def loadDataToMysql: Unit ={
    val resultSet = statement.executeQuery("select * from user_item.user_item_score limit 10")
    println(resultSet)

    while ( resultSet.next() ) {
      val user = resultSet.getString("user")
      val item = resultSet.getString("item")
      val score = resultSet.getString("score")
      println(user)
      println(item)
      println(score)

    }
  }
}





class AlsOnline {
    def main(args: Array[String]) {
// Load and parse the data

val logFile = "/user/root/ud.data" // Should be some file on your system
val conf = new SparkConf().setAppName("Als Application")
val sc = new SparkContext(conf)

val data = sc.textFile(logFile)


val ratings = data.map(_.split(',') match { case Array(user, item, rate) =>
  Rating(user.toInt, item.toInt, rate.toDouble)
})

//ratings.take(3)

// Build the recommendation model using ALS
val rank = 10
val numIterations = 10
val model = ALS.train(ratings, rank, numIterations, 0.01)

// Evaluate the model on rating data
val usersProducts = ratings.map { case Rating(user, product, rate) =>
  (user, product)
}
val predictions =
  model.predict(usersProducts).map { case Rating(user, product, rate) =>
    ((user, product), rate)
  }
val ratesAndPreds = ratings.map { case Rating(user, product, rate) =>
  ((user, product), rate)
}.join(predictions)
val MSE = ratesAndPreds.map { case ((user, product), (r1, r2)) =>
  val err = (r1 - r2)
  err * err
}.mean()
println("Mean Squared Error = " + MSE)

val user_196 =  model.predict(196,242)
println("user_196 movie is" + user_196)

// Save and load model
model.save(sc, "/user/root/targetmodel")
//val sameModel = MatrixFactorizationModel.load(sc, "/user/root/targetmodel")


val alpha = 0.01
val lambda = 0.01
val new_model = ALS.trainImplicit(ratings, rank, numIterations, lambda, alpha)

println("============== = " + new_model)
}
}
