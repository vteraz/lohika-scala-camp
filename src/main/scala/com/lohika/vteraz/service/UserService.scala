package com.lohika.vteraz.service

import cats.Monad
import cats.implicits._
import com.lohika.vteraz.model.{CreateUserRequest, UserModel}
import com.lohika.vteraz.persistence.repository.UserRepository

class UserService[F[_]](repository: UserRepository[F])(implicit monad: Monad[F]) {

  def registerUser(user: CreateUserRequest): F[Either[String, Long]] = {
    repository.getByUsername(user.username).flatMap({
      case Some(u) =>
        monad.pure(Left(s"User ${u.username} already exists"))
      case None =>
        repository.registerUser(UserModel(1, user.username, user.address, user.email)).map(Right(_))
    })
  }

  def getByUsername(username: String): F[Option[String]] = {
    repository.getByUsername(username).flatMap({
      case Some(user) =>
        monad.pure(Option(user.username))
      case None =>
        monad.pure(Option.empty)
    })
  }

  def getById(id: Long): F[Option[UserModel]] = {
    repository.getById(id).flatMap({
      case Some(user) =>
        monad.pure(Option(user))
      case None =>
        monad.pure(Option.empty)
    })
  }
}
