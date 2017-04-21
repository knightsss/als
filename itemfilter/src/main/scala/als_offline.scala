import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import org.apache.spark.mllib.recommendation.Rating

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf


object AlsOffline {
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
//model.save(sc, "/user/root/target2")
//val sameModel = MatrixFactorizationModel.load(sc, "/user/root/target2")


val alpha = 0.01
val lambda = 0.01
val new_model = ALS.trainImplicit(ratings, rank, numIterations, lambda, alpha)

println("============== = " + new_model)

}
}
