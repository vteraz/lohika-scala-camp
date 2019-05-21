package com.lohika.vteraz

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.dispatch.ExecutionContexts
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import cats.implicits._
import com.lohika.vteraz.repository.{DataSource, FutureInMemoryUserRepository, SlickH2UserRepository}
import com.lohika.vteraz.route.UserRoute
import com.lohika.vteraz.service.UserService

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

object Main {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = ExecutionContexts.fromExecutor(Executors.newCachedThreadPool())

    DataSource.init()

    val service = new UserService[Future](new SlickH2UserRepository())
    val userRoute = new UserRoute(service)

    Http().bindAndHandle(userRoute.routes, "localhost", 8080)

  }


  /**
    * Performs action specified amount of times and try to get suitable result.
    *
    * @param action        Action to perform
    * @param acceptResults Predicate to test whether result of action is valid
    * @param retries       Number of tries and timeout between them
    */
  @tailrec
  def retry[A](action: () => A,
               acceptResults: A => Boolean,
               retries: List[FiniteDuration]): A = {
    val result = action.apply()
    if (acceptResults.apply(result) || retries.isEmpty) result
    else {
      Thread.sleep(retries.head.toMillis)
      retry(action, acceptResults, retries.tail)
    }
  }

  def retryAsync[A](action: () => Future[A],
                    acceptResults: A => Boolean,
                    retries: List[FiniteDuration]): Future[A] = {
    action.apply().flatMap(actionResult => {
      if (acceptResults.apply(actionResult) || retries.isEmpty) {
        println(s"Stop retrying with value - $actionResult")
        Future(actionResult)
      }
      else {
        println(s"Value $actionResult is invalid. Running action one more time")
        Thread.sleep(retries.head.toMillis)
        retryAsync(action, acceptResults, retries.tail)
      }
    })
  }
}