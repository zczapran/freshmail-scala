package com.zczapran.freshmail.http

import akka.actor.ActorSystem
import com.zczapran.freshmail.model.Auth
import spray.http.HttpHeaders.{RawHeader, `Content-Type`}
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
  StatusCodes.registerCustom(555, "Subscriber already exist in this subscribers list")

  def send[T](
    method: HttpMethod,
    uri: Uri,
    obj: Option[T] = None
  )(implicit writer: JsonWriter[T]): Future[Result[Unit]] = {
    def handleEntity(entity: String): Result[Unit] = Try(entity.parseJson) match {
      case Success(value) if value.asJsObject.fields.contains("status") =>
        value.asJsObject.fields("status") match {
          case JsString("OK") => ().right
          case _ => ParseError(entity)
        }
      case _ => ParseError(entity)
    }

    call(method, uri, obj, handleEntity)
  }

  def request[T, U <: AnyRef : ClassTag](
    method: HttpMethod,
    uri: Uri,
    obj: Option[T] = None
  )(implicit writer: JsonWriter[T], reader: JsonReader[U]): Future[Result[U]] = {
    def handleEntity(entity: String): Result[U] = Try(entity.parseJson) match {
      case Success(value) if Set("status", "data") == value.asJsObject.fields.keySet =>
        val dataJson = value.asJsObject.fields("data")
        Try(dataJson.convertTo[U]) match {
          case Success(r) => r.right
          case _ => DeserializationError(dataJson, classTag[U].runtimeClass)
        }
      case _ => ParseError(entity)
    }

    call(method, uri, obj, handleEntity)
  }

  private def call[T, U](
    method: HttpMethod,
    uri: Uri,
    obj: Option[T] = None,
    handleEntity: String => Result[U]
  )(implicit writer: JsonWriter[T]): Future[Result[U]] = {
    val (auth, entity) = method match {
      case HttpMethods.GET =>
        Auth.build(apiKey, uri.path.toString(), "", apiSecret) -> HttpEntity.Empty
      case _ =>
        val payload = obj.map(_.toJson.compactPrint)
        Auth.build(apiKey, uri.path.toString(), payload.getOrElse(""), apiSecret) ->
          payload.map(HttpEntity(ContentType(MediaTypes.`application/json`), _)).getOrElse(HttpEntity.Empty)
    }

    val req: HttpRequest =
      HttpRequest(
        method = method,
        uri = uri,
        headers =
          RawHeader("X-Rest-ApiKey", auth.key) ::
          RawHeader("X-Rest-ApiSign", auth.sign) ::
          Nil,
        entity = entity
      )

    pipeline(req).map { res =>
      res.status match {
        case StatusCodes.NotFound => NotFound.left
        case _ =>
          val entity = res.entity.asString
          Try(entity.parseJson) match {
            case Success(value) if Set("status", "errors") == value.asJsObject.fields.keySet =>
              val errorJson = value.asJsObject.fields("errors")
              Try(errorJson.convertTo[ResponseError]) match {
                case Success(r) => r
                case _ => DeserializationError(errorJson, classTag[ResponseError].runtimeClass)
              }
            case _ => handleEntity(entity)
          }
      }
    }
  }

}

object Client {
  sealed trait Error
  case class ParseError(str: String) extends Error
  case class DeserializationError(json: JsValue, expected: Class[_]) extends Error
  case class ResponseError(fields: List[ResponseError.Field]) extends Error
  object ResponseError {
    case class Field(code: Int, message: String)
  }
  case object NotFound extends Error

  object ClientJsonProtocol extends DefaultJsonProtocol {
    implicit val responseErrorFieldReader: JsonFormat[ResponseError.Field] = jsonFormat2(ResponseError.Field.apply)
    implicit val responseErrorReader: JsonReader[ResponseError] = new JsonReader[ResponseError] {
      def read(value: JsValue): ResponseError = ResponseError(listFormat[ResponseError.Field].read(value))
    }
  }

  implicit def errorToResult[T](err: Error): Result[T] = err.left[T]

  type Result[T] = Error \/ T
}
