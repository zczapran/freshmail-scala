package com.zczapran.freshmail

import model.Subscriber
import spray.json.DefaultJsonProtocol._
import spray.json.JsonFormat


package object json {
  implicit val subscriberFormat: JsonFormat[Subscriber] = jsonFormat4(Subscriber.apply)
}
