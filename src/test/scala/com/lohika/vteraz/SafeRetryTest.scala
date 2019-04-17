package com.lohika.vteraz

import org.scalamock.scalatest.MockFactory
import org.scalatest.{DiagrammedAssertions, FunSuite}

import scala.concurrent.duration._

class SafeRetryTest extends FunSuite with DiagrammedAssertions with MockFactory {
  test("Test retry stopped when result is valid") {
    val result = Main.safeRetry[Int](() => 1 + 1, res => res % 2 == 0, List(0.seconds, 1.seconds))
    assert(result == 2)
  }

  test("Last result returned if all retries failed") {
    val mockAcceptResults = mockFunction[Int, Boolean]
    mockAcceptResults expects 2 returning false repeated 4 times

    val result = Main.safeRetry[Int](() => 1 + 1, mockAcceptResults, List(0.seconds, 1.seconds, 1.seconds, 1.seconds))
    assert(result == 2)
  }
}
