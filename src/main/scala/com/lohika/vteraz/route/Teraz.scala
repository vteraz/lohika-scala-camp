package com.lohika.vteraz.route

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import com.lohika.vteraz.model.CreateUserRequest
import com.lohika.vteraz.service.UserService

import scala.concurrent.Future

trait Teraz extends JsonSupport {

  implicit val userService: UserService[Future]

  val routes: Route =
    pathPrefix("user") {
      post {
        entity(as[CreateUserRequest]) { user =>
          onSuccess(userService.registerUser(user)) { result =>
            1 / 0
            complete(result.fold(
              errorMessage => HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(errorMessage)),
              id => s"${id.intValue}"))
          }
        }
      } ~ get {
        parameter("id") { userId =>
          onSuccess(userService.getById(userId.toLong)) {
            case Some(r) => complete(r)
            case None => complete(HttpResponse(StatusCodes.NotFound))
          }
        }
      }
    }


}
