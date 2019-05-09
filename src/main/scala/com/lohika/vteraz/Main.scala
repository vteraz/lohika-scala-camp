package com.lohika.vteraz

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object Main {

    def main(args: Array[String]): Unit = {
        val result = retry[Double](() => math.random(), res => res < 0.1, List(1.seconds, 2.seconds, 3.seconds))
        println(result)
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