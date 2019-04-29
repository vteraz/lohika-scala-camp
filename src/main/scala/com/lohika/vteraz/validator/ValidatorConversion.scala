package com.lohika.vteraz.validator

import com.lohika.vteraz.validator.Validator._

object ValidatorConversion {
    implicit val personValidator: Validator[Person] = isPersonValid
    implicit val stringValidator: Validator[String] = nonEmpty
    implicit val intValidator: Validator[Int] = positiveInt

    implicit def intToValidator(value: Int): ImplicitValidator[Int] = {
        new ImplicitValidator(value)
    }

    implicit def stringToValidator(value: String): ImplicitValidator[String] = {
        new ImplicitValidator(value)
    }

    implicit def personToValidator(value: Person): ImplicitValidator[Person] = {
        new ImplicitValidator(value)
    }
}

class ImplicitValidator[T](value: T) {
    def validate(implicit validator: Validator[T]): Either[String, T] = {
        validator.validate(value)
    }
}
