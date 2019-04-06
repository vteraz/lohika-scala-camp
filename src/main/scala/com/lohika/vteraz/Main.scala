package com.lohika.vteraz

import com.lohika.vteraz.service.CrimeService.CrimeDto
import com.lohika.vteraz.data.{CsvReader, CsvReaderImpl}
import com.lohika.vteraz.service.CrimeService

object Main {
  def main(args: Array[String]): Unit = {
    implicit val fileReader: CsvReader = new CsvReaderImpl(getFilePath(args))

    val data: Seq[(String, List[CrimeDto])] = CrimeService.getTop5TheftLocations()

    data.foreach(data => {
      println("-----------------------------------------------------------------------------------")
      println(s"${data._1}: ${data._2.size}")
      println("Thefts:")
      data._2.foreach(theft => println(s"ID: ${theft.id}"))
    })
  }

  private def getFilePath(args: Array[String]): String = {
    if(args.length < 1) {
      Console.err.println("Path to file with Crimes should be provided")
      sys.exit(1)
    }
    args(0)
  }
}