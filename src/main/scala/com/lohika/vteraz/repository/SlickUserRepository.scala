package com.lohika.vteraz.repository

import com.lohika.vteraz.Model.User

import scala.concurrent.Future

class SlickUserRepository extends UserRepository[Future] {
  override def registerUser(username: String): Future[User] = ???

  override def getById(id: Long): Future[Option[User]] = ???

  override def getByUsername(username: String): Future[Option[User]] = ???
}
