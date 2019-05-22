package com.lohika.vteraz.route

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.{ExceptionHandler, MalformedRequestContentRejection, RejectionHandler, ValidationRejection}
import com.lohika.vteraz.DbException
import com.lohika.vteraz.model.ErrorResponseModel
import com.typesafe.scalalogging.LazyLogging
import spray.json.DefaultJsonProtocol.{jsonFormat3, _}
import spray.json.RootJsonFormat

object ErrorHandlingSupport extends LazyLogging {
  implicit val errorResponseJsonFormat: RootJsonFormat[ErrorResponseModel] = jsonFormat3(ErrorResponseModel)

  implicit def getExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case dbException: DbException =>
        complete(InternalServerError.intValue, ErrorResponseModel(InternalServerError.intValue, "DB_EXCEPTION", dbException.getMessage))
      case a: Exception =>
        complete(InternalServerError.intValue, ErrorResponseModel(InternalServerError.intValue, "GENERAL_EXCEPTION", a.getMessage))
    }

  implicit def getRejectionHandler: RejectionHandler =

    RejectionHandler.newBuilder()
      .handle { case MalformedRequestContentRejection(message, _) =>
        complete(StatusCodes.BadRequest.intValue, ErrorResponseModel(StatusCodes.BadRequest.intValue, "BAD_REQUEST", message))
      case ValidationRejection(message, _) => {
        complete(StatusCodes.BadRequest.intValue,
          ErrorResponseModel(StatusCodes.BadRequest.intValue, "BAD_REQUEST", message))
      }
      case _ =>
        complete(StatusCodes.BadRequest.intValue, ErrorResponseModel(StatusCodes.BadRequest.intValue, "BAD_REQUEST", ""))
      }
      .result()

}
