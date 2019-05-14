package com.lohika.vteraz

import cats.implicits._
import com.lohika.vteraz.repository.FutureInMemoryUserRepository
import com.lohika.vteraz.service.UserService
import org.scalatest.AsyncFlatSpec

import scala.concurrent.Future

class UserServiceAsyncTest extends AsyncFlatSpec {
    it should "register new user" in {
        val userService = new UserService[Future](new FutureInMemoryUserRepository)

        userService.registerUser("Tom").map(u => {
            assert(u.isRight)
        })
    }

    it should "fail to register when user exists" in {
        val userService = new UserService[Future](new FutureInMemoryUserRepository)

        userService.registerUser("Tom").map(u => {
            userService.registerUser("Tom").map(u => {
                assert(u.isLeft)
            })
            assert(u.isRight)
        })
    }


}
