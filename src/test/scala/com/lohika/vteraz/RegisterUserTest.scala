package com.lohika.vteraz

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpEntity, StatusCodes, _}
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler, Route}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import cats.implicits._
import com.lohika.vteraz.model.{CreateUserRequest, ErrorResponseModel}
import com.lohika.vteraz.persistence.DataSource
import com.lohika.vteraz.persistence.repository.SlickH2UserRepository
import com.lohika.vteraz.route.{ErrorHandlingSupport, UserRoute}
import com.lohika.vteraz.service.UserService
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import spray.json.DefaultJsonProtocol.{jsonFormat3, _}

import scala.concurrent.Future

class RegisterUserTest extends WordSpec with Matchers with ScalaFutures with BeforeAndAfterEach
  with ScalatestRouteTest {
  implicit val exceptionHandler: ExceptionHandler = ErrorHandlingSupport.getExceptionHandler
  implicit val rejectionHandler: RejectionHandler = ErrorHandlingSupport.getRejectionHandler
  implicit val userService: UserService[Future] = new UserService[Future](new SlickH2UserRepository(DataSource.db))
  implicit val errorResponseJsonFormat = jsonFormat3(ErrorResponseModel)

  val userRoute = new UserRoute(userService)
  DataSource.init()

  "User route" should {
    "successfully register user and return ID if request is valid" in {
      val jsonRequest = ByteString(
        s"""
           |{
           |    "username":"User1",
           |    "email": "user@mail.com",
           |    "address": "Some Street"
           |}
        """.stripMargin)

      val createUserRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/user",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest))

      createUserRequest ~> userRoute.routes ~> check {
        status shouldEqual StatusCodes.Created
        responseAs[String] shouldEqual "1"
      }
    }
    "return BadRequest in case username already exists" in {
      userService.registerUser(CreateUserRequest("John", Some("a"), "user@mail.com"))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "username":"John",
           |    "email": "user@mail.com",
           |    "address": "Some Street"
           |}
          """.stripMargin)

      val createUserRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/user1",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest))

      createUserRequest ~> Route.seal(userRoute.routes) ~> check {
        responseAs[String] shouldEqual "User John already exists"
        status shouldEqual StatusCodes.BadRequest
      }
    }
    "return BadRequest in case username is not specified" in {
      val jsonRequest = ByteString(
        s"""
           |{
           |    "email": "user@mail.com",
           |    "address": "Some Street"
           |}
          """.stripMargin)

      val createUserRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/user1",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest))

      createUserRequest ~> Route.seal(userRoute.routes) ~> check {
        status shouldEqual StatusCodes.BadRequest
        responseAs[ErrorResponseModel] shouldEqual ErrorResponseModel(400,"BAD_REQUEST","Object is missing required member 'username'")
      }
    }
    "return BadRequest in case username contain special chars" in {
      val jsonRequest = ByteString(
        s"""
           |{
           |    "username": "user#",
           |    "email": "user@mail.com",
           |    "address": "Some Street"
           |}
          """.stripMargin)

      val createUserRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/user1",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest))

      createUserRequest ~> Route.seal(userRoute.routes) ~> check {
        status shouldEqual StatusCodes.BadRequest
        responseAs[String] shouldEqual "requirement failed: username must not be empty AND contain only alphanumeric characters"
      }
    }
    "return BadRequest in case email is not well-formatted" in {
      val jsonRequest = ByteString(
        s"""
           |{
           |    "username": "User#@",
           |    "email": "user_mail.com",
           |    "address": "Some Street"
           |}
          """.stripMargin)

      val createUserRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/user1",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest))

      createUserRequest ~> Route.seal(userRoute.routes) ~> check {
        status shouldEqual StatusCodes.BadRequest
        responseAs[String] shouldEqual "requirement failed: username must not be empty AND contain only alphanumeric characters"
      }
    }
  }

}
