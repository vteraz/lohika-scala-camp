package com.lohika.vteraz.repository

import java.util.concurrent.atomic.AtomicLong

import com.lohika.vteraz.Model.User

import scala.concurrent.Future

class FutureInMemoryUserRepository extends UserRepository[Future] {
    private val storage = scala.collection.mutable.Map[Long, User]()
    private val idGenerator: AtomicLong = new AtomicLong()

    override def registerUser(username: String): Future[User] = {
        Future.successful {
            val id = idGenerator.incrementAndGet()
            val user = User(id, username, None, "")
            println(storage)
            storage.put(id, user)
            user
        }
    }

    override def getById(id: Long): Future[Option[User]] = Future.successful(storage.get(id))

    override def getByUsername(username: String): Future[Option[User]] =
        Future.successful(storage.values.find(x => x.username.equals(username)))
}
