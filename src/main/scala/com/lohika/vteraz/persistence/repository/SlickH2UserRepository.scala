package com.lohika.vteraz.persistence.repository

import com.lohika.vteraz.model.UserModel
import com.lohika.vteraz.persistence.entity.UserTable
import com.lohika.vteraz.service.RetryService
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future
import scala.concurrent.duration._

class SlickH2UserRepository(db: Database) extends UserRepository[Future] {
  val usersTable = TableQuery[UserTable]

  override def registerUser(user: UserModel): Future[Long] = {
    val userId = (usersTable returning usersTable.map(_.id)) += user
    RetryService.retryAsync[Long](() => db.run(userId), id => !id.isNaN, List(0.seconds, 1.seconds, 5.seconds))

  }

  override def getById(id: Long): Future[Option[UserModel]] = {
    db.run(usersTable.filter(_.id === id).result.headOption)
  }

  override def getByUsername(username: String): Future[Option[UserModel]] = {
    db.run(usersTable.filter(_.username === username).result.headOption)
  }
}
