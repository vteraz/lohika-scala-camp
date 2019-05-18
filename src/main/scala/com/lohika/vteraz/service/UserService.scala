package com.lohika.vteraz.service

import cats.Monad
import cats.implicits._
import com.lohika.vteraz.Model.User
import com.lohika.vteraz.repository.UserRepository

class UserService[F[_]](repository: UserRepository[F])(implicit monad: Monad[F]) {

  def registerUser(username: String): F[Either[String, User]] = {
    repository.getByUsername(username).flatMap({
      case Some(user) =>
        monad.pure(Left(s"User ${user.username} already exists"))
      case None =>
        repository.registerUser(username).map(Right(_))
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

  def getById(id: Long): F[Option[User]] = {
    repository.getById(id).flatMap({
      case Some(user) =>
        monad.pure(Option(user))
      case None =>
        monad.pure(Option.empty)
    })
  }
}
