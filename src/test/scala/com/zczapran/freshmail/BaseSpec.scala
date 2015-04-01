package com.zczapran.freshmail

import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{Matchers, FlatSpec}


trait BaseSpec extends FlatSpec with Matchers with ScalaFutures {
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))
  val config = ConfigFactory.load()
}
