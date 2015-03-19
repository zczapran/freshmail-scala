package com.zczapran.freshmail.http

import akka.actor.ActorSystem
import spray.client.pipelining._
import spray.http.{HttpResponse, HttpRequest}
import concurrent.Future


class DefaultPipeline(implicit val system: ActorSystem) {
  import system.dispatcher

  val apply: HttpRequest => Future[HttpResponse] = sendReceive
}
