package com.lohika.vteraz.persistence.entity

import com.lohika.vteraz.model.UserModel
import slick.jdbc.H2Profile.api._

class UserTable(tag: Tag) extends Table[UserModel](tag, "user") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def username = column[String]("name")
  def address = column[Option[String]]("address")
  def email = column[String]("email")

  def * = (id, username, address, email).mapTo[UserModel]
}