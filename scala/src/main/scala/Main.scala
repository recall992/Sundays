import com.alibaba.fastjson.JSON
import org.apache.spark.{SparkConf, SparkContext}

object Main {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("WordCount").setMaster("local")
    val sc = new SparkContext(conf)
    val line = sc.textFile("/home/sundays/Desktop/新建文本 4.txt")
    line.map(_.split(",")(0)).foreach(println)
  }
}
