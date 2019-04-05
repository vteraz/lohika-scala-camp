package com.lohika.vteraz

import com.lohika.vteraz.service.CrimeService.CrimeDto
import com.lohika.vteraz.data.{CsvReader, CsvReaderImpl}
import com.lohika.vteraz.service.CrimeService

object Main {
  def main(args: Array[String]): Unit = {
    implicit val fileReader: CsvReader = new CsvReaderImpl(args(0))

    val data: Seq[(String, List[CrimeDto])] = CrimeService.getTop5TheftLocations()

    data.foreach(data => {
      println("-----------------------------------------------------------------------------------")
      println(s"${data._1}: ${data._2.size}")
      println("Thefts:")
      data._2.foreach(theft => println(s"ID: ${theft.id}"))
    })
  }
}
