package com.lohika.vteraz

import cats.Id
import com.lohika.vteraz.repository.{IdInMemoryIotDeviceRepository, IdInMemoryUserRepository}
import com.lohika.vteraz.service.{IotDeviceService, UserService}
import org.scalatest.FunSuite

class IoTServiceTest extends FunSuite {
    test("register device successfully") {
        val userRepo = new IdInMemoryUserRepository
        new UserService[Id](userRepo)
        val iotService = new IotDeviceService[Id](new IdInMemoryIotDeviceRepository, userRepo)
        userRepo.registerUser("Tom")
        val result = iotService.registerDevice(1, "11")

        assert(result.isRight)
        assert(result.right.get.id == 1)
    }

    test("register device - user not exists") {
        val userRepo = new IdInMemoryUserRepository
        new UserService[Id](userRepo)
        val iotService = new IotDeviceService[Id](new IdInMemoryIotDeviceRepository, userRepo)
        val result = iotService.registerDevice(1, "11")

        assert(result.isLeft)
        assert(result.left.get == "User with ID 1 not found")
    }

    test("register device - sn already exists") {
        val userRepo = new IdInMemoryUserRepository
        new UserService[Id](userRepo)
        val iotService = new IotDeviceService[Id](new IdInMemoryIotDeviceRepository, userRepo)
        userRepo.registerUser("Tom")
        iotService.registerDevice(1, "11")
        val result = iotService.registerDevice(1, "11")

        assert(result.isLeft)
        assert(result.left.get == "Device with sn 11 already exists")
    }
}
