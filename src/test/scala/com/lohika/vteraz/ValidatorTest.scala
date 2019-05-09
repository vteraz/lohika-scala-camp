package com.lohika.vteraz

import com.lohika.vteraz.validator.Person
import com.lohika.vteraz.validator.Validator._
import com.lohika.vteraz.validator.ValidatorConversion._
import org.scalatest.FunSuite

class ValidatorTest extends FunSuite {
    test("isPositive Left if negative number specified") {
        val result = positiveInt.validate(-2)
        assert(!result.isRight)
        assert(result.isLeft)
        assert(result.left.get == "Number -2 is not positive")
    }

    test("isPositive Right if positive number specified") {
        val result = positiveInt.validate(12)
        assert(result.isRight)
        assert(!result.isLeft)
        assert(result.right.get == 12)
    }

    test("lessThan Right") {
        val result = lessThan(20).validate(12)
        assert(result.isRight)
        assert(!result.isLeft)
        assert(result.right.get == 12)
    }

    test("lessThan Left") {
        val result = lessThan(20).validate(120)
        assert(!result.isRight)
        assert(result.isLeft)
        assert(result.left.get == "Number 120 is not less than 20")
    }

    test("nonEmpty Left") {
        val result = nonEmpty.validate("")
        assert(!result.isRight)
        assert(result.isLeft)
        assert(result.left.get == "Specified string is empty")
    }

    test("nonEmpty Left for blank string") {
        val result = nonEmpty.validate("        ")
        assert(!result.isRight)
        assert(result.isLeft)
        assert(result.left.get == "Specified string is empty")
    }

    test("nonEmpty Right") {
        val result = nonEmpty.validate("Test")
        assert(result.isRight)
        assert(!result.isLeft)
        assert(result.right.get == "Test")
    }

    test("person valid") {
        val testPerson = Person("John", 50)
        val result = isPersonValid.validate(testPerson)
        assert(result.isRight)
        assert(!result.isLeft)
        assert(result.right.get.eq(testPerson))
    }

    test("person invalid - empty name") {
        val testPerson = Person("", 50)
        val result = isPersonValid.validate(testPerson)
        assert(!result.isRight)
        assert(result.isLeft)
        assert(result.left.get == "Specified string is empty")
    }

    test("person invalid - age is not positive int") {
        val testPerson = Person("John", -20)
        val result = isPersonValid.validate(testPerson)
        assert(!result.isRight)
        assert(result.isLeft)
        assert(result.left.get == "Number -20 is not positive")
    }

    test("person invalid - age is to big") {
        val testPerson = Person("John", 220)
        val result = isPersonValid.validate(testPerson)
        assert(!result.isRight)
        assert(result.isLeft)
        assert(result.left.get == "Number 220 is not less than 100")
    }

    test("AND validator is valid") {
        val result = positiveInt.and(lessThan(100)).validate(20)
        assert(result.isRight)
        assert(!result.isLeft)
        assert(result.right.get == 20)
    }

    test("AND validator is invalid") {
        val result = positiveInt.and(lessThan(100)).validate(2000)
        assert(!result.isRight)
        assert(result.isLeft)
        assert(result.left.get == "Number 2000 is not less than 100")
    }

    test("AND validator is invalid 2") {
        val result = positiveInt.and(lessThan(100)).validate(-2000)
        assert(!result.isRight)
        assert(result.isLeft)
        assert(result.left.get == "Number -2000 is not positive")
    }

    test("OR validator - both predicates are valid") {
        val result = positiveInt.or(lessThan(100)).validate(20)
        assert(result.isRight)
        assert(!result.isLeft)
        assert(result.right.get == 20)
    }

    test("OR validator - both predicates are invalid") {
        val result = positiveInt.or(lessThan(-100)).or(lessThan(-50)).validate(-20)
        assert(!result.isRight)
        assert(result.isLeft)
        assert(result.left.get == "Number -20 is not positive OR Number -20 is not less than -100 OR Number -20 is not less than -50")
    }

    test("OR validator - second predicate is valid") {
        val result = positiveInt.or(lessThan(100)).validate(-20)
        assert(result.isRight)
        assert(!result.isLeft)
        assert(result.right.get == -20)
    }

    test("OR validator - first predicate is valid") {
        val result = positiveInt.or(lessThan(100)).validate(220)
        assert(result.isRight)
        assert(!result.isLeft)
        assert(result.right.get == 220)
    }

    test("test Int implicit conversion") {
        val result = 22 validate (positiveInt and lessThan(10))
        assert(!result.isRight)
        assert(result.isLeft)
        assert(result.left.get == "Number 22 is not less than 10")

        val result1 = 22 validate lessThan(10)
        assert(!result1.isRight)
        assert(result1.isLeft)
        assert(result1.left.get == "Number 22 is not less than 10")
    }

    test("test Person implicit conversion") {
        val result = Person(name = "John", age = 25) validate isPersonValid
        assert(result.isRight)
        assert(!result.isLeft)
    }

    test("test Person implicit validator") {
        val result = Person(name = "John", age = 25) validate

        assert(result.isRight)
        assert(!result.isLeft)
    }
}
