package com.zczapran.freshmail.model

case class Auth(
  key: String,
  sign: String
)

object Auth {
  def build(key: String, path: String, json: String, secret: String) = {
    val md = java.security.MessageDigest.getInstance("SHA-1")
    Auth(
      key = key,
      sign = md.digest((key ++ path ++ json ++ secret).getBytes("UTF-8")).map("%02x".format(_)).mkString
    )
  }
}
