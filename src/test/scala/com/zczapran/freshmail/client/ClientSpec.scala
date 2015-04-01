package com.zczapran.freshmail.client

import akka.actor.ActorSystem
import com.zczapran.freshmail.BaseSpec
import com.zczapran.freshmail.http.Client.ResponseError
import com.zczapran.freshmail.http.Client.ResponseError.Field
import com.zczapran.freshmail.http.{DefaultPipeline, Client}
import spray.http.HttpMethods
import spray.json.DefaultJsonProtocol._

import scalaz.{\/-, -\/}


class ClientSpec extends BaseSpec {
  implicit val system = ActorSystem("test-system")
  val pipeline = new DefaultPipeline

  "Client" should "make a successful ping request and receive pong" in {
    val client = new Client(config.getString("client.api-key"), config.getString("client.api-secret"), pipeline.apply)

    val r = client.request[String, String](
      HttpMethods.POST, "https://api.freshmail.com/rest/ping", Some("COS"))

    whenReady(r) {
      _ should be(\/-("pong"))
    }
  }

  it should "handle authorization error" in {
    val client = new Client("", "", pipeline.apply)

    val r = client.request[String, String](
      HttpMethods.POST, "https://api.freshmail.com/rest/ping", Some("COS"))

    whenReady(r) {
      _ should be(-\/(ResponseError(List(Field(1000, "Brak autoryzacji")))))
    }
  }

}
