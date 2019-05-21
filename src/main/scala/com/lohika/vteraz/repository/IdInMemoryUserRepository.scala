package com.lohika.vteraz.repository

import java.util.concurrent.atomic.AtomicLong

import cats.Id
import com.lohika.vteraz.model.UserModel

class IdInMemoryUserRepository {
    private val storage = scala.collection.mutable.Map[Long, UserModel]()
    private val idGenerator: AtomicLong = new AtomicLong()

    def registerUser(username: String): Id[UserModel] = {
        val id = idGenerator.incrementAndGet()
        val user = UserModel(id, username, None, "")
        storage.put(id, user)
        user
    }

    def getById(id: Long): Id[Option[UserModel]] = storage.get(id)

    def getByUsername(username: String): Id[Option[UserModel]] = storage.values.find(x => x.username.equals(username))
}
