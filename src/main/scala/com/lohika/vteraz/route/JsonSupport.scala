package com.lohika.vteraz.route

import com.lohika.vteraz.Model.CreateUserRequest
import com.lohika.vteraz.model.UserModel
import spray.json.DefaultJsonProtocol._

trait JsonSupport {
  implicit val userJsonFormat = jsonFormat4(UserModel)
  implicit val createUserRequestJsonFormat = jsonFormat3(CreateUserRequest)
}
