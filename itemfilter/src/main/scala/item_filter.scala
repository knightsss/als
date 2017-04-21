import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import scala.math._

object ItemFilter {
    def main(args: Array[String]) {

val logFile = "/user/root/tencent_wb_300000_new.txt" // Should be some file on your hdfs system
val conf = new SparkConf().setAppName("Als Application")
val sc = new SparkContext(conf)

//RDD 化文件
val textfile = sc.textFile(logFile)

//分隔符切分，获取user item的Array
var user_item = textfile.map(_.split(",") match { case Array(user,item) => (user,item)})

//基于user分组并获取item的值，此时是集合的形式，并转换成iterator
var item_iterator = user_item.groupByKey().values.map((s) => s.iterator)

//将iterator转换成List
var item_set_list = item_iterator.map((s) => s.toList)

//对一行的List做类似笛卡尔积，目的构造二维矩阵
var item_item = item_set_list.flatMap((s) => for (i <- 0 until s.length) yield( for (j <- i+1 until s.length)yield(s(i),s(j)) ))
var item_item_set = item_item.flatMap((s) => for (i <- s)yield(i))

//对两个在同一组的item分组求和
var item_item_count = item_item_set.map((s) => (s,1)).reduceByKey(_+_)


//item_item_count.collect().foreach(println)


//构造item-用户结构，用来计算每个item被喜欢的人数
var item_user = textfile.map(_.split(",") match { case Array(user,item) => (item,user)})

//转换成item,喜欢人的数目
var item_user_number = item_user.groupByKey().map((s) => (s._1,s._2.iterator.toList.length))

//item_user_number.collect().foreach(println)
//定义空map
var item_usernumber_map = Map[String,Int]()

//var item_usernumber_map:Map[String,Int] = Map()


//将item喜欢的人数("item1",10)RDD类型转换成scala的Map
for (x <- item_user_number.collect()){ item_usernumber_map += (x._1 -> x._2)}
//print (item_usernumber_map)

//计算距离

var item_item_distinct = item_item_count.map((s) => (s._1,(s._2.toFloat/sqrt((item_usernumber_map(s._1._1).toFloat * item_usernumber_map(s._1._2).toFloat)))))

print ("calc start!")
var user="zhangxiaowuahn"
//指定用户1001的item list获取
//for(user <- user_item.groupByKey.map((s) => s._1).collect() ){
    var item_list = user_item.filter((s) => s._1==user).map((s) => s._2)collect()

    //求一个user的item的相似的item,条件是user,获取该user所有的item_list
    var user_item_like_items = item_item_distinct.filter((s) => item_list.contains(s._1._1)).map((s) => (s._1._2,s._2)).reduceByKey((x,y) => x+y).filter((s) => !item_list.contains(s._1))

    user_item_like_items.sortBy(_._2,false).take(5)foreach(println)
//}
}
}
