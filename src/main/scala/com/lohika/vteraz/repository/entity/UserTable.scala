package com.lohika.vteraz.repository.entity

import com.lohika.vteraz.Model.User
import slick.jdbc.H2Profile.api._

//class UserTable(tag: Tag) extends Table[User](tag, "user") {
//    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
//
//    def name = column[String]("name")
//
//    def * = (id, name, address, ) <> (User.tupled, User.unapply)
//
//}