package com.lohika.vteraz.repository

import com.lohika.vteraz.Model.User

trait UserRepository[F[_]] {
    def registerUser(username: String): F[User]

    def getById(id: Long): F[Option[User]]

    def getByUsername(username: String): F[Option[User]]
}