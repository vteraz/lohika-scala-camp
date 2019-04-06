package com.lohika.vteraz.data

import scala.io.Source


class CsvReaderImpl(filePath: String) extends CsvReader {

  override def readFile(): List[String] = {
    try {
      Source.fromFile(filePath).getLines().drop(1).toList
    } catch {
      case e: Exception => {
        Console.err.println("Failed to read file: " + e.getMessage)
        sys.exit(1)
      }
    }
  }
}
