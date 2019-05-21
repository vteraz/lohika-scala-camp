package com.lohika.vteraz.repository

import com.lohika.vteraz.model.UserModel

trait UserRepository[F[_]] {
  def registerUser(user: UserModel): F[Long]

  def getById(id: Long): F[Option[UserModel]]

  def getByUsername(username: String): F[Option[UserModel]]
}