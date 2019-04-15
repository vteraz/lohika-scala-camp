package com.lohika.vteraz

import com.typesafe.scalalogging.LazyLogging
import scalaj.http.{Http, HttpResponse}

import scala.annotation.tailrec
import scala.concurrent.duration._

object Main extends LazyLogging {

  val randomHttpRequest = () => {
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
    retry[Int](randomHttpRequest, res => res == 200, List(0.seconds, 5.seconds, 5.seconds))
    retry[Double](() => math.random(), res => res < 0.1, List(0.seconds, 1.seconds, 1.seconds))
  }

  @tailrec
  def retry[A](action: () => A,
               acceptResults: A => Boolean,
               retries: List[FiniteDuration]): A = {
    retries match {
      case Nil => throw new RuntimeException(s"Failed to perform action")
      case x :: tail =>
        logger.info(s"Sleeping $x and performing action")
        Thread.sleep(x.toMillis)
        val result = action.apply()
        if (acceptResults.apply(result)) result else retry(action, acceptResults, tail)
    }
  }
}