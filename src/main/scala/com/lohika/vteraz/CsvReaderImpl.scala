package com.lohika.vteraz

import scala.io.Source

class CsvReaderImpl(filePath: String) extends CsvReader {
  /**
    * Read file and returns List of files lines
    *
    * @return List that contains all lines from file
    */
 override def readFile(): List[String] = {
    Source.fromFile(filePath).getLines().drop(1).toList
  }
}
