package com.lohika.vteraz.route

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.lohika.vteraz.Model.User

object UserRoute extends JsonSupport {
  val routes: Route =
    pathPrefix("user") {
      post {
        entity(as[User]) { user =>
          complete(user)
        }
      } ~ get {
        parameter("id") { userId =>
          complete(User(Option(1L), "233", Option("fsdfds"), "terazvasyl@gmai.com"))
        }
      }
    }
}
