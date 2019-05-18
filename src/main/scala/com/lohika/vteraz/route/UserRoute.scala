package com.lohika.vteraz.route

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.lohika.vteraz.Model.CreateUserRequest
import com.lohika.vteraz.service.UserService

import scala.concurrent.Future

class UserRoute(userService: UserService[Future]) extends JsonSupport {

  val routes: Route =
    pathPrefix("user") {
      post {
        entity(as[CreateUserRequest]) { user =>
          onSuccess(userService.registerUser(user.username)) { result =>
            complete(result.fold(
              errorMessage => HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(errorMessage)),
              registeredUser => registeredUser))
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