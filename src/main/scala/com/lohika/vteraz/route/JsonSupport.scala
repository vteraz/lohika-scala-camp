package com.lohika.vteraz.route

import com.lohika.vteraz.Model.User
import spray.json.DefaultJsonProtocol._

trait JsonSupport {
  implicit val userJsonFormat = jsonFormat4(User)
}
