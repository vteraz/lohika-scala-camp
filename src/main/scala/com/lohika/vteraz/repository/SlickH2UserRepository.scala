package com.lohika.vteraz.repository

import com.lohika.vteraz.Model.User
import com.lohika.vteraz.repository.DataSource.{db, usersTable}
import com.lohika.vteraz.repository.entity.UserTable
import slick.jdbc.{JdbcBackend, JdbcProfile}
import slick.lifted.TableQuery

import scala.concurrent.Future

class SlickH2UserRepository extends UserRepository[Future] {
  val usersTable = TableQuery[UserTable]

  override def registerUser(user: User): Future[User] = {
    DataSource.db.run(usersTable += User(1, "John", Option("Lviv"), "user@gmail.com"))

    Future.successful(user)
  }

  override def getById(id: Long): Future[Option[User]] = ???

  override def getByUsername(username: String): Future[Option[User]] = ???
}
