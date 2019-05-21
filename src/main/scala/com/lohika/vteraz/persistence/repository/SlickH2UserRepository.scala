package com.lohika.vteraz.persistence.repository

import com.lohika.vteraz.model.UserModel
import com.lohika.vteraz.persistence.entity.UserTable
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

class SlickH2UserRepository(db: Database) extends UserRepository[Future] {
  val usersTable = TableQuery[UserTable]

  override def registerUser(user: UserModel): Future[Long] = {
    val userId = (usersTable returning usersTable.map(_.id)) += user
    db.run(userId)
  }

  override def getById(id: Long): Future[Option[UserModel]] = {
    db.run(usersTable.filter(_.id === id).result.headOption)
  }

  override def getByUsername(username: String): Future[Option[UserModel]] = {
    db.run(usersTable.filter(_.username === username).result.headOption)
  }
}
