package com.zczapran.freshmail.service

import com.zczapran.freshmail.http.Client
import com.zczapran.freshmail.http.Client.Result
import com.zczapran.freshmail.json._
import com.zczapran.freshmail.model.Subscriber
import spray.http.HttpMethods

import concurrent.{ExecutionContext, Future}


class SubscriberService(client: Client) {

  def subscribe(email: String, listHash: String)(implicit ec: ExecutionContext): Future[Result[Unit]] =
    client.send[Subscriber](
      HttpMethods.POST, "https://api.freshmail.com/rest/subscriber/add",
      Some(Subscriber(email, listHash, 1, Some(true)))
    )

}
