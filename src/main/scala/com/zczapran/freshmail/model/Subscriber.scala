package com.zczapran.freshmail.model


case class Subscriber(
  email: String,
  list: String,
  state: Int,
  confirm: Option[Boolean]
)
