package com.lohika.vteraz.data

import scala.io.Source


class CsvReaderImpl(filePath: String) extends CsvReader {

  override def readFile(): List[String] = {
    Source.fromFile(filePath).getLines().drop(1).toList
  }
}
