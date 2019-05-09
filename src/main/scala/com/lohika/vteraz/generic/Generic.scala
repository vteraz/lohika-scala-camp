package com.lohika.vteraz.generic

import java.util.concurrent.atomic.AtomicLong

import cats.implicits._
import cats.{Id, Monad}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

case class User(id: Long, username: String)

case class IotDevice(id: Long, userId: Long, sn: String)

trait UserRepository[F[_]] {
    def registerUser(username: String): F[User]

    def getById(id: Long): F[Option[User]]

    def getByUsername(username: String): F[Option[User]]
}

trait IotDeviceRepository[F[_]] {
    def registerDevice(userId: Long, serialNumber: String): F[IotDevice]

    def getById(id: Long): F[Option[IotDevice]]

    def getBySn(sn: String): F[Option[IotDevice]]

    def getByUser(userId: Long): F[Seq[IotDevice]]
}

class InMemoryUserRepository extends UserRepository[Id] {
    private val storage = scala.collection.mutable.Map[Long, User]()
    private val idGenerator: AtomicLong = new AtomicLong()

    override def registerUser(username: String): Id[User] = {
        val id = idGenerator.incrementAndGet()
        val user = User(id, username)
        storage.put(id, user)
        user
    }

    override def getById(id: Long): Id[Option[User]] = storage.get(id)

    override def getByUsername(username: String): Id[Option[User]] = storage.values.find(x => x.username.equals(username))
}

class AsyncUserRepository extends UserRepository[Future] {
    private val storage = scala.collection.mutable.Map[Long, User]()
    private val idGenerator: AtomicLong = new AtomicLong()

    override def registerUser(username: String): Future[User] = {
        Future.successful {
            val id = idGenerator.incrementAndGet()
            val user = User(id, username)
            println(storage)
            storage.put(id, user)
            user
        }
    }

    override def getById(id: Long): Future[Option[User]] = Future.successful(storage.get(id))

    override def getByUsername(username: String): Future[Option[User]] =
        Future.successful(storage.values.find(x => x.username.equals(username)))
}

class InMemoryIotDeviceRepository extends IotDeviceRepository[Id] {
    var storage: Map[Long, IotDevice] = Map()
    private val idGenerator: AtomicLong = new AtomicLong()

    override def registerDevice(userId: Long, serialNumber: String): Id[IotDevice] = {
        val id = idGenerator.incrementAndGet()
        val device = IotDevice(id, userId, serialNumber)
        storage = storage + {
            id -> device
        }
        device
    }

    override def getById(id: Long): Id[Option[IotDevice]] = {
        storage.values.find(d => d.id.equals(id))
    }

    override def getBySn(sn: String): Id[Option[IotDevice]] = {
        storage.values.find(d => d.sn.equals(sn))
    }

    override def getByUser(userId: Long): Id[Seq[IotDevice]] = {
        storage.values.filter(d => d.userId.equals(userId)).toSeq
    }
}

class AsyncIoTDeviceRepository extends IotDeviceRepository[Future] {
    var storage: Map[Long, IotDevice] = Map()
    private val idGenerator: AtomicLong = new AtomicLong()

    override def registerDevice(userId: Long, serialNumber: String): Future[IotDevice] = {
        Future.successful {
            val id = idGenerator.incrementAndGet()
            val device = IotDevice(id, userId, serialNumber)
            storage = storage + {
                id -> device
            }
            device
        }
    }

    override def getById(id: Long): Future[Option[IotDevice]] = {
        Future.successful(storage.values.find(d => d.id.equals(id)))
    }

    override def getBySn(sn: String): Future[Option[IotDevice]] = {
        Future.successful(storage.values.find(d => d.sn.equals(sn)))
    }

    override def getByUser(userId: Long): Future[Seq[IotDevice]] = {
        Future.successful(storage.values.filter(d => d.userId.equals(userId)).toSeq)
    }
}

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

class IotDeviceService[F[_]](repository: IotDeviceRepository[F],
                             userRepository: UserRepository[F])
                            (implicit monad: Monad[F]) {

    // the register should fail with Left if the user doesn't exist or the sn already exists.
    def registerDevice(userId: Long, sn: String): F[Either[String, User]] = {
        userRepository.getById(userId).flatMap({
            case Some(user) =>
                repository.getBySn(sn).flatMap({
                    case Some(_) => monad.pure(Left(s"Device with sn $sn already exists"))
                    case None => repository.registerDevice(userId, sn).map(_ => Right(user))
                })
            case None => monad.pure(Left(s"User with ID $userId not found"))
        })
    }
}
