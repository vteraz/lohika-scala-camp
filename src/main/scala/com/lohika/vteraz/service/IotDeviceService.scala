package com.lohika.vteraz.service

import cats.Monad
import cats.implicits._
import com.lohika.vteraz.Model.User
import com.lohika.vteraz.repository.{IotDeviceRepository, UserRepository}

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
