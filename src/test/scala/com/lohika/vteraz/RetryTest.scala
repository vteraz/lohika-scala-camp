package com.lohika.vteraz

import org.scalamock.scalatest.MockFactory
import org.scalatest.{DiagrammedAssertions, FunSuite}

import scala.concurrent.duration._

class RetryTest extends FunSuite with DiagrammedAssertions with MockFactory{
  test("Test retry stopped when result is valid") {
    val result = Main.retry[Int](() => 1 + 1, res => res % 2 == 0, List(0.seconds, 1.seconds))
    assert(result == 2)
  }

  test("Test retry stopped when result is valid") {
    val mockAcceptResult =  mockFunction[Int, Boolean]
    mockAcceptResult expects 2 returning true

    val result = Main.retry[Int](() => 1 + 1, mockAcceptResult, List(0.seconds, 1.seconds))
    assert(result == 2)
  }

  test("Test exception thrown when all retries failed") {
    intercept[RuntimeException] {
      Main.retry[Int](() => 1 + 1, res => res % 3 == 0, List(0.seconds, 1.seconds))
    }
  }
}
