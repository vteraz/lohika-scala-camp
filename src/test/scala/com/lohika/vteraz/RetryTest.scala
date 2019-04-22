package com.lohika.vteraz

import org.scalamock.scalatest.MockFactory
import org.scalatest.{DiagrammedAssertions, FunSuite}

import scala.concurrent.duration._

class RetryTest extends FunSuite with DiagrammedAssertions with MockFactory {
    test("Test if first result valid, then no retries") {
        val mockAcceptResults = mockFunction[Int, Boolean]
        mockAcceptResults expects 2 returning true repeated 1 times

        val result = Main.retry[Int](() => 1 + 1, mockAcceptResults, List(0.seconds, 1.seconds))
        assert(result == 2)
    }

    test("Test if all retries invalid, then last result returned") {
        val mockAcceptResults = mockFunction[Int, Boolean]
        mockAcceptResults expects 2 returning false repeated 3 times

        val result = Main.retry[Int](() => 1 + 1, mockAcceptResults, List(0.seconds, 1.seconds))
        assert(result == 2)
    }

    test("Test no retries specified - valid result") {
        val mockAcceptResults = mockFunction[Int, Boolean]
        mockAcceptResults expects 2 returning true repeated 1 times

        val result = Main.retry[Int](() => 1 + 1, mockAcceptResults, List.empty)
        assert(result == 2)
    }

    test("Test no retries specified - invalid result") {
        val mockAcceptResults = mockFunction[Int, Boolean]
        mockAcceptResults expects 2 returning false repeated 1 times

        val result = Main.retry[Int](() => 1 + 1, mockAcceptResults, List.empty)
        assert(result == 2)
    }
}
