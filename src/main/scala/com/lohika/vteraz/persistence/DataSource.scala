package com.lohika.vteraz.persistence

import java.util.concurrent.Executors

import com.lohika.vteraz.persistence.entity.UserTable
import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

object DataSource extends LazyLogging{
  implicit val executionContext: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))
  val db: H2Profile.backend.Database = Database.forConfig("h2")
  val usersTable = TableQuery[UserTable]


  def init() {
    logger.info("Bootstrap DB")
    db.run(usersTable.schema.create).onComplete(_ => logger.info("Bootstrap finished"))
  }
}
