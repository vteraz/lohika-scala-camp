package com.lohika.vteraz

import com.lohika.vteraz.Main.retryAsync
import org.scalatest.AsyncFlatSpec

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.duration._

class AsyncRetryTest extends AsyncFlatSpec {

    it should "return result without retrying" in {
        var counter = 0
        val invocationHistory = List((System.currentTimeMillis(), counter))
        val testAction = () => Future {
            counter += 1
            (System.currentTimeMillis(), counter) :: invocationHistory
            counter
        }
        val futureActionResult: Future[Int] = retryAsync[Int](testAction, res => res == 1, List(1.seconds, 2.seconds, 3.seconds))

        futureActionResult map { result => {
            println(invocationHistory)
            assert(invocationHistory.size == 1)
            assert(invocationHistory.head._2 == 0)
            assert(result == 1)
        } }
    }

    it should "return result after second retry" in {
        var counter = 0
        val invocationHistory = ListBuffer((System.currentTimeMillis(), counter))
        val testAction = () => Future {
            counter += 1
            invocationHistory.append((System.currentTimeMillis(), counter))
            counter
        }
        val futureActionResult: Future[Int] = retryAsync[Int](testAction, res => res == 2, List(1.seconds, 2.seconds, 3.seconds))

        futureActionResult map { result => {
            assert(result == 2)
            val invocationsTimes = invocationHistory.map(invocation => invocation._1).toList
            assert(invocationsTimes(1) - invocationsTimes.head < 500, "Invalid period between first and second invocation")
            assert(invocationsTimes(2) - invocationsTimes(1) >= 1000, "Invalid period between second and third invocation")
            assert(invocationHistory.size == 3, "Invalid invocation count")
            assert(invocationHistory.head._2 == 0)
            assert(invocationHistory(1)._2 == 1)
            assert(invocationHistory(2)._2 == 2)
        } }
    }

    it should "return last result if all retries failed" in {
        var counter = 0
        val invocationHistory = ListBuffer((System.currentTimeMillis(), counter))
        val action = () => Future {
            counter += 1
            invocationHistory.append((System.currentTimeMillis(), counter))
            counter
        }
        val futureResult: Future[Int] = retryAsync[Int](action, res => res == 100, List(1.seconds, 2.seconds, 4.seconds))

        futureResult map { result => {
            assert(result == 4)
            val invocationsTimes = invocationHistory.map(invocation => invocation._1).toList
            assert(invocationsTimes(1) - invocationsTimes.head < 500, "Invalid period between first and second invocation")
            assert(invocationsTimes(2) - invocationsTimes(1) >= 1000, "Invalid period between second and third invocation")
            assert(invocationsTimes(2) - invocationsTimes(1) < 1500, "Invalid period between second and third invocation")
            assert(invocationsTimes(3) - invocationsTimes(2) >= 2000, "Invalid period between second and third invocation")
            assert(invocationsTimes(3) - invocationsTimes(2) < 2500, "Invalid period between second and third invocation")
            assert(invocationsTimes(4) - invocationsTimes(3) >= 4000, "Invalid period between second and third invocation")
            assert(invocationHistory.size == 5, "Invalid invocation count")
            assert(invocationHistory.head._2 == 0)
            assert(invocationHistory(1)._2 == 1)
            assert(invocationHistory(2)._2 == 2)
            assert(invocationHistory(3)._2 == 3)
            assert(invocationHistory(4)._2 == 4)
        } }
    }
}
