package com.lohika.vteraz.data

/**
  * Provides ability to read all lines in file
  * Implementations should return List[String] where every element corresponds to line in file
  */
trait CsvReader {
  /**
    * Read file and returns List of files lines
    *
    * @return List that contains all lines from file
    */
  def readFile(): List[String]
}
