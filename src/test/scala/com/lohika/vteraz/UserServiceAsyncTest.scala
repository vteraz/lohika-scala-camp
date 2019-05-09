package com.lohika.vteraz

import com.lohika.vteraz.generic.{AsyncUserRepository, UserService}
import org.scalatest.{AsyncFlatSpec, FunSuite}

import scala.concurrent.Future
import cats.implicits._
import cats.{Id, Monad}

class UserServiceAsyncTest extends AsyncFlatSpec {
    it should "register new user" in {
        val userService = new UserService[Future](new AsyncUserRepository)

        userService.registerUser("Tom").map(u => {
            assert(u.isRight)
        })
    }

    it should "fail to register when user exists" in {
        val userService = new UserService[Future](new AsyncUserRepository)

        userService.registerUser("Tom").map(u => {
            userService.registerUser("Tom").map(u => {
                assert(u.isLeft)
            })
            assert(u.isRight)
        })
    }


}
