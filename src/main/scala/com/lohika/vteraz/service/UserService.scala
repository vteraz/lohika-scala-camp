package com.lohika.vteraz.service

import cats.implicits._
import cats.Monad
import com.lohika.vteraz.repository.{User, UserRepository}

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

    def getById(id: Long): F[Option[String]] = {
        repository.getById(id).flatMap({
            case Some(user) =>
                monad.pure(Option(user.username))
            case None =>
                monad.pure(Option.empty)
        })
    }
}
