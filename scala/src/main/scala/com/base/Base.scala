package com.base

import scala.collection.mutable.ArrayBuffer

object Base {

  //基本的数组,map,touple操作
  def main(args: Array[String]): Unit = {
    println("Array test*************")
    arrayTest()
    println("Map test*************")
    mapTest()
  }

  def arrayTest(): Unit = {
    // Array
    val arrStr = Array("spark", "hadoop")
    val arrInt = new Array[Int](10)
    //不定长数组
    val arrayBuffer = ArrayBuffer[Int]()

    arrStr(0) = "strom"
    println(arrStr.mkString("-"))
    //定长数组转换为不定长数组
    val arrStrBuff = arrStr.toBuffer

    //遍历
    for (i <- 0 until arrStr.length) {
      println(arrStr(i))
    }
    for (e <- arrStr) {
      println(e)
    }

    //可变数组后结尾添加元素
    arrayBuffer += 1
    arrayBuffer += (2, 3, 4, 5)
    arrayBuffer ++= Array(1, 2, 3)
    arrayBuffer.toArray.foreach(println(_))
  }

  def mapTest(): Unit = {
    val bigData = Map("scala" -> 2, "Spark" -> 1, "Hadoop" -> 3, "Flink" -> 5)
    println(bigData("scala"))
    println(bigData.contains("Spark"))
    println(bigData.contains("SparK"))
    println(bigData.mkString("@"))
    println(bigData.mkString("[", "#", "]"))
    println(bigData.drop(1))
    println(bigData.mkString("[", "#", "]"))
  }

  def listTest(): Unit = {
    val fruit = List("Spark", "Scala", "Hadoop")
    val bigData: List[String] = List("Spark", "Scala", "Hadoop")

  }
}
