package com.zczapran.freshmail.model


case class Subscriber(
  email: String,
  list: String,
  state: Option[Subscriber.State],
  confirm: Option[Boolean]
)

object Subscriber {
  trait State { val value: Int }
  case object Active extends State { val value = 1 }
  case object ToBeActivated extends State { val value = 2 }
  case object NonActivated extends State { val value = 3 }
  case object SignedOut extends State { val value = 4 }
  case object SoftBouncing extends State { val value = 5 }
  case object HardBouncing extends State { val value = 8 }
}
