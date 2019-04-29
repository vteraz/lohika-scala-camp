package com.lohika.vteraz.validator

/**
  * Implement validator typeclass that should validate arbitrary value [T].
  *
  * @tparam T the type of the value to be validated.
  */
trait Validator[T] {
    /**
      * Validates the value.
      *
      * @param value value to be validated.
      * @return Right(value) in case the value is valid, Left(message) on invalid value
      */
    def validate(value: T): Either[String, T]

    /**
      * And combinator.
      *
      * @param other validator to be combined with 'and' with this validator.
      * @return the Right(value) only in case this validator and <code>other</code> validator returns valid value,
      *         otherwise Left with error messages from the validator that failed.
      */
    def and(other: Validator[T]): Validator[T] = {
        value: T => {
            val firstResult = validate(value)
            if (firstResult.isLeft) {
                Left(firstResult.left.get)
            } else {
                val secondResult = other.validate(value)
                if (secondResult.isLeft) {
                    Left(secondResult.left.get)
                } else {
                    Right(value)
                }
            }
        }
    }

    /**
      * Or combinator.
      *
      * @param other validator to be combined with 'or' with this validator.
      * @return the Right(value) only in case either this validator or <code>other</code> validator returns valid value,
      *         otherwise Left with error messages from both validators.
      */
    def or(other: Validator[T]): Validator[T] = {
        value: T => {
            val firstResult = validate(value)
            val secondResult = other.validate(value)
            val groupedResults = List(firstResult, secondResult).groupBy(result => result.isRight)

            if (groupedResults.contains(true)) {
                Right(value)
            } else {
                Left(groupedResults(false).map(r => r.left.get).mkString(" OR "))
            }
        }
    }
}


object Validator {
    val positiveInt: Validator[Int] = (t: Int) => {
        if (t > 0) Right(t)
        else Left(s"Number $t is not positive")
    }

    def lessThan(n: Int): Validator[Int] = (t: Int) => {
        if (t < n) Right(t)
        else Left(s"Number $t is not less than $n")
    }

    val nonEmpty: Validator[String] = (t: String) => {
        if (!t.isBlank) Right(t)
        else Left(s"Specified string is empty")
    }

    val isPersonValid: Validator[Person] = new Validator[Person] {
        // Returns valid only when the name is not empty and age is in range [1-99].
        override def validate(value: Person): Either[String, Person] = {
            val isNonEmpty = nonEmpty.validate(value.name)
            if (isNonEmpty.isLeft) return Left(isNonEmpty.left.get)

            val agePositiveInt = positiveInt.validate(value.age)
            if (agePositiveInt.isLeft) return Left(agePositiveInt.left.get)

            val ageInValidBound = lessThan(100).validate(value.age)
            if (ageInValidBound.isLeft) return Left(ageInValidBound.left.get)

            Right(value)
        }
    }
}

object ValidApp {
    import com.lohika.vteraz.validator.Validator._
    import com.lohika.vteraz.validator.ValidatorConversion._

    2 validate (positiveInt and lessThan(10))

    "" validate nonEmpty

    Person(name = "John", age = 25) validate isPersonValid
}

object ImplicitValidApp {
    import com.lohika.vteraz.validator.ValidatorConversion._

    Person(name = "John", age = 25) validate

    234.validate

    "" validate
}


case class Person(name: String, age: Int)
