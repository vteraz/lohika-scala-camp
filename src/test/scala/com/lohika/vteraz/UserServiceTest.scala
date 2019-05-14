package com.lohika.vteraz

import cats.Id
import com.lohika.vteraz.repository.IdInMemoryUserRepository
import com.lohika.vteraz.service.UserService
import org.scalatest.FunSuite

class UserServiceTest extends FunSuite {
    test("Test successfully register new user") {
        val userService = new UserService[Id](new IdInMemoryUserRepository)

        val newUser = userService.registerUser("John")
        assert(newUser.isRight)
        assert(newUser.right.get.username.equals("John"))
        assert(newUser.right.get.id == 1)
    }

    test("Test user already exists") {
        val userService = new UserService[Id](new IdInMemoryUserRepository)

        userService.registerUser("John")
        val newUser = userService.registerUser("John")
        assert(newUser.isLeft)
        assert(newUser.left.get == "User John already exists")
    }

    test("Test get user by name") {
        val userService = new UserService[Id](new IdInMemoryUserRepository)

        userService.registerUser("John")

        assert(userService.getByUsername("John").isDefined)
        assert(userService.getByUsername("John1").isEmpty)
    }

    test("Test get user by id") {
        val userService = new UserService[Id](new IdInMemoryUserRepository)

        var u = userService.registerUser("John")

        assert(userService.getById(u.right.get.id).isDefined)
        assert(userService.getById(100).isEmpty)
    }

}
