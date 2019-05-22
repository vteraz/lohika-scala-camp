package com.lohika.vteraz.route

import com.lohika.vteraz.model.{CreateUserRequest, ErrorResponseModel, UserModel}
import spray.json.DefaultJsonProtocol._

trait JsonSupport {
  implicit val userJsonFormat = jsonFormat4(UserModel)
  implicit val createUserRequestJsonFormat = jsonFormat3(CreateUserRequest)
  implicit val errorResponseJsonFormat = jsonFormat3(ErrorResponseModel)
}
