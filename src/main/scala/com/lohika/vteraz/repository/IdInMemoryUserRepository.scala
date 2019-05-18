package com.lohika.vteraz.repository

import java.util.concurrent.atomic.AtomicLong

import cats.Id
import com.lohika.vteraz.Model.User

class IdInMemoryUserRepository extends UserRepository[Id] {
    private val storage = scala.collection.mutable.Map[Long, User]()
    private val idGenerator: AtomicLong = new AtomicLong()

    override def registerUser(username: String): Id[User] = {
        val id = idGenerator.incrementAndGet()
        val user = User(id, username, None, "")
        storage.put(id, user)
        user
    }

    override def getById(id: Long): Id[Option[User]] = storage.get(id)

    override def getByUsername(username: String): Id[Option[User]] = storage.values.find(x => x.username.equals(username))
}
