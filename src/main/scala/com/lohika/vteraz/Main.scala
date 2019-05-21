package com.lohika.vteraz

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.dispatch.ExecutionContexts
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler}
import akka.stream.ActorMaterializer
import cats.implicits._
import com.lohika.vteraz.persistence.DataSource
import com.lohika.vteraz.persistence.repository.SlickH2UserRepository
import com.lohika.vteraz.route.{ErrorHandlingSupport, UserRoute}
import com.lohika.vteraz.service.UserService

import scala.concurrent.{ExecutionContextExecutor, Future}

object Main {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = ExecutionContexts.fromExecutor(Executors.newCachedThreadPool())
    implicit val exceptionHandler: ExceptionHandler = ErrorHandlingSupport.getExceptionHandler
    implicit val rejectionHandler: RejectionHandler = ErrorHandlingSupport.getRejectionHandler

    DataSource.init()

    val service = new UserService[Future](new SlickH2UserRepository(DataSource.db))
    val userRoute = new UserRoute(service)

    Http().bindAndHandle(userRoute.routes, "localhost", 8080)
  }
}