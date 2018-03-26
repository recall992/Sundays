package com.base

object BaseTest {

  def main(args: Array[String]): Unit = {
    val bt = new BaseTest
    bt.pri
    println(bt.concat(bt))
  }

  private[base] class BaseTest {
    private[this] val a = "NiHao"
    private[base] val b = "Hi"

    def pri = {
      println(this.a)
    }

    def concat(bt: BaseTest) = {
      bt.b + " Scala"
    }
  }

}

