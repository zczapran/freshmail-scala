package com.zczapran.freshmail.http

import akka.actor.ActorSystem
import spray.http.HttpHeaders.`Content-Type`
import spray.http._
import spray.json._
import reflect._
import scalaz.\/
import scalaz.Scalaz.ToEitherOps
import concurrent.{ExecutionContext, Future}
import Client._
import Client.ClientJsonProtocol.responseErrorReader

import util.{Success, Try}

class Client(
  apiKey: String,
  apiSecret: String,
  pipeline: HttpRequest => Future[HttpResponse]
)(implicit val system: ActorSystem, ec: ExecutionContext) {

  def request[T, U <: AnyRef : ClassTag](
    method: HttpMethod,
    uri: Uri,
    obj: T
  )(implicit writer: JsonWriter[T], reader: JsonReader[U]): Future[Result[U]] = {

    val entity = method match {
      case HttpMethods.GET => HttpEntity.Empty
      case _ => HttpEntity(ContentTypes.`application/json`, obj.toJson.compactPrint)
    }

    val req: HttpRequest =
      HttpRequest(
        method = method,
        uri = uri,
        headers = `Content-Type`(ContentTypes.`application/json`) :: Nil,
        entity = entity
      )

    def handleEntity(entity: String): Result[U] = Try(entity.parseJson) match {
      case Success(value) if Set("status", "data") == value.asJsObject.fields.keySet =>
        val dataJson = value.asJsObject.fields("data")
        Try(dataJson.convertTo[U]) match {
          case Success(r) => r.right
          case _ => DeserializationError(dataJson, classTag[U].runtimeClass)
        }
      case Success(value) if Set("status", "errors") == value.asJsObject.fields.keySet =>
        val errorJson = value.asJsObject.fields("data")
        Try(errorJson.convertTo[ResponseError]) match {
          case Success(r) => r
          case _ => DeserializationError(errorJson, classTag[U].runtimeClass)
        }

      case _ => ParseError(entity)
    }

    pipeline(req).map { res =>
      res.status match {
        case StatusCodes.NotFound => NotFound.left
        case _ => handleEntity(res.entity.asString)
      }
    }
  }

}

object Client {
  sealed trait Error
  case class ParseError(str: String) extends Error
  case class DeserializationError(json: JsValue, expected: Class[_]) extends Error
  case class ResponseError(code: Int, message: String) extends Error
  case object NotFound extends Error

  object ClientJsonProtocol extends DefaultJsonProtocol {
    implicit val responseErrorReader: JsonReader[ResponseError] = jsonFormat2(ResponseError.apply)
  }

  implicit def errorToResult[T](err: Error): Result[T] = err.left[T]

  type Result[T] = Error \/ T
}
