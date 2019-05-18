package com.lohika.vteraz

import com.lohika.vteraz.repository.entity.UserTable
import slick.jdbc.H2Profile.api._
import slick.lifted.OptionMapper2

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object Main {

    def main(args: Array[String]): Unit = {
        val db = Database.forURL("jdbc:h2:mem:hello", driver = "org.h2.Driver", keepAliveConnection = true)

        val users = TableQuery[UserTable]

        class Coffees(tag: Tag) extends Table[(String, Double)](tag, "COFFEES") {
            def name = column[String]("COF_NAME")
            def price = column[Double]("PRICE")
            def * = (name, price)
        }
        val coffees = TableQuery[Coffees]

        val q = users.filter(_.id > 8).map(_.name)

        val setup = DBIO.seq(
            users.schema.create,
            users += (1, "Tom"),
            users += (2, "Jerry")
        )

        val setupFuture = db.run(setup)


        Thread.sleep(20000)
    }


    /**
      * Performs action specified amount of times and try to get suitable result.
      *
      * @param action        Action to perform
      * @param acceptResults Predicate to test whether result of action is valid
      * @param retries       Number of tries and timeout between them
      */
    @tailrec
    def retry[A](action: () => A,
                 acceptResults: A => Boolean,
                 retries: List[FiniteDuration]): A = {
        val result = action.apply()
        if (acceptResults.apply(result) || retries.isEmpty) result
        else {
            Thread.sleep(retries.head.toMillis)
            retry(action, acceptResults, retries.tail)
        }
    }

    def retryAsync[A](action: () => Future[A],
                      acceptResults: A => Boolean,
                      retries: List[FiniteDuration]): Future[A] = {
        action.apply().flatMap(actionResult => {
            if (acceptResults.apply(actionResult) || retries.isEmpty) {
                println(s"Stop retrying with value - $actionResult")
                Future(actionResult)
            }
            else {
                println(s"Value $actionResult is invalid. Running action one more time")
                Thread.sleep(retries.head.toMillis)
                retryAsync(action, acceptResults, retries.tail)
            }
        })
    }
}