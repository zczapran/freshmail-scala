package com.zczapran.freshmail.service

import com.zczapran.freshmail.http.Client
import com.zczapran.freshmail.model.Subscriber
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import spray.http.HttpMethods
import spray.json._
import DefaultJsonProtocol._

trait SubscriberServiceSpec extends FlatSpec with Matchers with ScalaFutures {

  "SubscriberService" should "register new subscriber" in {
    val subscriber = new Subscriber("zczapran@citylovers.io", "xyz", 1, Some(true))

//    val r = client.send[Subscriber](
//      HttpMethods.POST, "https://api.freshmail.com/rest/subscriber/add", Some(subscriber))
//
//    whenReady(r) { result =>
//      println(result)
//    }
//

  }

}
