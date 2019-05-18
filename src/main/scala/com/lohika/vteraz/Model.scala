package com.lohika.vteraz

import com.lohika.vteraz.validator.Validator.{alphanumericOnly, nonEmpty, wellFormattedEmail}

object Model {

  case class User(id: Option[Long], username: String, address: Option[String], email: String) {
    require(nonEmpty.and(alphanumericOnly).validate(username).isRight,
      "username must not be empty AND contain only alphanumeric characters")
    address match {
      case Some(a) => require(nonEmpty.validate(a).isRight, "a must not be empty")
      case _ =>
    }

    require(wellFormattedEmail.validate(email).isRight, "invalid email format")
  }

}
