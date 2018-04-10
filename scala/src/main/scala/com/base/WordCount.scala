package com.base

import com.alibaba.fastjson.JSON
import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("WordCount").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val line = sc.textFile("/home/sundays/Desktop/bee9f657-490b-4e6f-aa04-4d7dd0bb7708.gz")
    val str = "{\"user\":\"sundays-word-test\"}";
    line.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).foreach(x => {
      println(x)
    })
    println(JSON.parseObject(str).getString("user"))
  }
}
