package com.lohika.vteraz.repository
import java.util.concurrent.Executors

import com.lohika.vteraz.Model.User
import com.lohika.vteraz.repository.entity.UserTable
import slick.dbio.Effect
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import slick.sql.FixedSqlAction

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

object DataSource {
  implicit val executionContext: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))
  val db: H2Profile.backend.Database = Database.forConfig("h2")
  val usersTable = TableQuery[UserTable]

  def init() {
    println("Bootstrap DB")

    val result = for {
      _ <- db.run(usersTable.schema.create)
      insertUserResult <- db.run(usersTable += User(1, "John", Option("Lviv"), "user@gmail.com"))
      insertUserResult2 <- db.run(usersTable += User(1, "Tom", Option("London"), "user@gmail.com"))
    } yield insertUserResult2

    println(result)

    val data = db.run(usersTable.result)

    Thread.sleep(2000)
    println(data)

  }
}
