package com.base

object FunctionHigh {
  def func1(n: Int): Int = {
    val myFunc = (i: Int, x: Int) => i * x
    myFunc.apply(n, 2)
  }

  //将函数作为参数，参数类型为int，返回结果类型为int
  def func2(x: Int => Int) = x

  //高阶函数
  def func3(f: (Int, Int) => Int) = f

  //匿名函数应用
  def func4(x: Int) = x + 1

  //函数是一个匿名函数
  def func5(x: Int): Int = {
    if (x < 1) x else x * func5(x - 1)
  }

  //函数字面量的多个占位符使用
  val func6 = (_: Int) + (_: Int)

  def func7(a: Int, b: Int, c: Int) = a + b + c

  val func8 = func7 _
  //偏应用函数
  val func9 = func7(1, _: Int, 3)

  //定义函数闭包
  def func10(x: Int) = (y: Int) => x + y

  def func11(args: Int*) = for (arg <- args) println(arg)

  def func12(x: Int): Int = {
    if (x == 0)
      throw new Exception("The Exception")
    else
      func12(x - 1)
  }

  def hello1(m: Int): Int = m

  def hello2(m: Int, n: Int): Int = m * n

  def main(args: Array[String]): Unit = {
    println(func1(78))
    println(func2(hello1)(2))
    println(func3(hello2)(2, 3))
    println(func4(3))
    println(func4(1))
    func6(7, 2)
    func8(1, 89, 3)
    func9.apply(8)
    func10(12)(2)
    func11(List(1, 2, 3): _*)
  }
}
