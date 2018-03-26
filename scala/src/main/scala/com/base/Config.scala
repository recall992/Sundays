package com.base

import org.apache.commons.configuration.ConfigurationFactory
import org.apache.log4j.PropertyConfigurator

object Config {
  def main(args: Array[String]): Unit = {
    println(max(0))

    def funCurring(x: Int) = (y: Int) => (z: Int) => x * y + z

    val step1 = funCurring(3)
    val step2 = step1(3)
    val step3 = step2(2)
    println(step3)
  }

  def max(n: Int): Int = {
    if (n == 0) 1
    else n * max(n - 1)
  }
}
