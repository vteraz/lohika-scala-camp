package com.lohika.vteraz

import com.lohika.vteraz.Main.FailedToPerformActionException
import com.typesafe.scalalogging.LazyLogging
import scalaj.http.{Http, HttpResponse}

import scala.annotation.tailrec
import scala.concurrent.duration._

object Main extends LazyLogging {

  val randomHttpRequest: () => Int = () => {
    val randomUrl = "http://httpbin.org/status/100,200,300,400,500"
    logger.info(s"Performing request to $randomUrl")

    try {
      val response: HttpResponse[String] = Http(randomUrl).timeout(2000, 5000).asString
      logger.info(s"Response code - ${response.code}")

      response.code
    } catch {
      case _: Exception => -1
    }
  }

  def main(args: Array[String]): Unit = {
    safeRetry[Int](randomHttpRequest, res => res == 200, List(0.seconds, 2.seconds, 4.seconds))

    /////////////////////////////////////////////////////////////////////////////
    try {
      retry[Double](() => math.random(), res => res < 0.1, List(0.seconds, 1.seconds, 1.seconds))
    } catch {
      case _: FailedToPerformActionException => println("Unable to perform action after 3 retries")
    }

    //////////////////////////////////////////////////////////////////////////////
    retry[Double](() => math.random(), res => res < 0.1, List(0.seconds, 1.seconds, 1.seconds))
  }

  /**
    * Performs action specified amount of times and try to get suitable result.
    *
    * <b>If no suitable result found - throws exception</b>
    *
    * @param action        Action to perform
    * @param acceptResults Predicate to test whether result of action is valid
    * @param retries       Number of tries and timeout between them
    * @throws FailedToPerformActionException In case no valid result achieved
    */
  @tailrec
  def retry[A](action: () => A,
               acceptResults: A => Boolean,
               retries: List[FiniteDuration]): A = {
    retries match {
      case Nil => throw new FailedToPerformActionException()
      case x :: tail =>
        logger.info(s"Sleeping $x and performing action")
        Thread.sleep(x.toMillis)
        val result = action.apply()
        if (acceptResults.apply(result)) result else retry(action, acceptResults, tail)
    }
  }

  /**
    * Performs action specified amount of times and try to get suitable result.
    *
    * <b>If no suitable result found - returns last result of action no matter whether it's correct</b>
    *
    * @param action        Action to perform
    * @param acceptResults Predicate to test whether result of action is valid
    * @param retries       Number of tries and timeout between them
    */
  @tailrec
  def safeRetry[A](action: () => A,
                   acceptResults: A => Boolean,
                   retries: List[FiniteDuration]): A = {
    retries match {
      case x :: tail =>
        logger.info(s"Sleeping $x and performing action")
        Thread.sleep(x.toMillis)
        val result = action.apply()
        if (acceptResults.apply(result) || tail.isEmpty) result else safeRetry(action, acceptResults, tail)
    }
  }

  class FailedToPerformActionException() extends RuntimeException("Failed to perform action") {}
}