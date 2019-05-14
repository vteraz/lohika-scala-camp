package com.lohika.vteraz.repository

case class User(id: Long, username: String)

trait UserRepository[F[_]] {
    def registerUser(username: String): F[User]

    def getById(id: Long): F[Option[User]]

    def getByUsername(username: String): F[Option[User]]
}