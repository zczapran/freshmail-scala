package com.zczapran.freshmail.service

import akka.actor.ActorSystem
import com.zczapran.freshmail.http.{Client, DefaultPipeline}
import com.zczapran.freshmail.model.Subscriber
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import spray.http.HttpMethods
import spray.json._
import DefaultJsonProtocol._
import org.scalatest.concurrent.PatienceConfiguration._
import time.{Seconds, Millis, Span}

class SubscriberServiceTest extends FlatSpec with Matchers with ScalaFutures {

  implicit val system = ActorSystem("test-system")
  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  "SubscriberService" should "register new subscriber" in {
//    val pipeline = new DefaultPipeline
//    import scala.concurrent.ExecutionContext.Implicits.global
//
//    val client = new Client("", "", pipeline.apply)
//    val subscriber = new Subscriber("zczapran@citylovers.io", "aslba62hjj", 1, Some(true))
//
//    implicit val subscriberFormat: JsonFormat[Subscriber] = jsonFormat4(Subscriber.apply)
//
//    val r = client.send[Subscriber](
//      HttpMethods.POST, "https://api.freshmail.com/rest/subscriber/add", Some(subscriber))
//
//    whenReady(r) { result =>
//      println(result)
//    }

//    val r2 = client.request[String, String](
//      HttpMethods.POST, "https://api.freshmail.com/rest/ping", Some("COS"))
//
//    whenReady(r2) { result =>
//      println(result)
//    }

  }

}
