package com.lohika.vteraz

import com.typesafe.scalalogging.LazyLogging
import scalaj.http.{Http, HttpResponse}

import scala.annotation.tailrec
import scala.concurrent.duration._

object Main extends LazyLogging {

  val randomHttpRequest = () => {
    val randomUrl = "http://httpbin.org/status/100,200,300,400,500"
    logger.info(s"Performing request to $randomUrl")

    val response: HttpResponse[String] = Http(randomUrl).timeout(2000, 5000).asString
    logger.info(s"Response code - ${response.code}")

    response.code
  }

  def main(args: Array[String]): Unit = {
    retry[Int](randomHttpRequest, res => res == 200, List(0.seconds, 2.seconds, 5.seconds))
    retry[Double](() => math.random(), res => res < 0.5, List(0.seconds, 1.seconds, 5.seconds))
  }

  @tailrec
  def retry[A](block: () => A,
               acceptResults: A => Boolean,
               retries: List[FiniteDuration]): A = {
    logger.info(s"Sleep ${retries.head} and perform action")
    Thread.sleep(retries.head.toMillis)
    val retriesTail = retries.tail
    val result = block.apply()
    if (acceptResults.apply(result)) {
      logger.info(s"Result $result is acceptable")
      result
    } else if (retriesTail.isEmpty) {
      throw new RuntimeException("Failed to perform action " + block)
    } else {
      logger.info(s"Result $result is unacceptable. Performing action on more time.")
      retry(block, acceptResults, retriesTail)
    }
  }
}