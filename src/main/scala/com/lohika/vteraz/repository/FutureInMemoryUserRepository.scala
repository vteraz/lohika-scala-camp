package com.lohika.vteraz.repository

import java.util.concurrent.atomic.AtomicLong

import com.lohika.vteraz.model.UserModel

import scala.concurrent.Future

class FutureInMemoryUserRepository {
    private val storage = scala.collection.mutable.Map[Long, UserModel]()
    private val idGenerator: AtomicLong = new AtomicLong()

    def registerUser(username: String): Future[UserModel] = {
        Future.successful {
            val id = idGenerator.incrementAndGet()
            val user = UserModel(id, username, None, "")
            println(storage)
            storage.put(id, user)
            user
        }
    }

    def getById(id: Long): Future[Option[UserModel]] = Future.successful(storage.get(id))

    def getByUsername(username: String): Future[Option[UserModel]] =
        Future.successful(storage.values.find(x => x.username.equals(username)))
}
