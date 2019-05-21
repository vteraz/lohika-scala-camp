package com.lohika.vteraz.repository.entity

import com.lohika.vteraz.Model.User
import slick.jdbc.H2Profile.api._

class UserTable(tag: Tag) extends Table[User](tag, "user") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def username = column[String]("name")
  def address = column[Option[String]]("address")
  def email = column[String]("email")

  def * = (id, username, address, email).mapTo[User]
}