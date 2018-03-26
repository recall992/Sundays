package com.base

object ExtractorTest {

  def apply(user: String, domain: String) {
    user + "@" + domain
  }

  def unapply(str: String): Option[String] = {
    val parts = str.split("@")
    if (parts.length == 2) {
      Some(parts(0))
    } else {
      Some(parts(0))
    }
  }
}
