package com.base

//object 定义对象为单例对象，对于同名的object和class对象互称伴生对象和伴生类，伴生对象和伴生类必须在同一个文件中。
//伴生对象可以访问伴生类的静态属性
object Person {
  println("Person")
  var age = 10

  def getAge = age
}

class Person(name: String) {
  println(this.name)

  def this(name: String, age: Int) {
    this(name)
    println(name + "\t" + age)
  }

  def this(name: String, age: Int, set: String) {
    this(name, age)
    println(name + "\t" + age)
  }
}