package com.base

abstract class Animal {
  def eat = {
    println("Animal eat food!!")
  }

  def run
}

class Cat extends Animal {
  override def eat: Unit = {
    println("Cat eat mouse!!!")
  }

  override def run: Unit = {
    println("Cat Running....")
  }
}

class Dog extends Animal {
  override def run: Unit = {
    println("Dog Running...")
  }
}

//子类不能重写父类中被final修饰的方法和属性
object AnimalTest {
  def main(args: Array[String]): Unit = {
    val cat = new Cat
    cat.eat
    cat.run
    val dog = new Dog
    dog.eat
    dog.run
  }
}