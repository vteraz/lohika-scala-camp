package com.lohika.vteraz

import com.lohika.vteraz.data.{CsvReader, CsvReaderImpl}
import org.scalatest._

class CsvReaderImplTest extends FunSuite with DiagrammedAssertions {
  test("All lines in file, except header, was read") {
    val testFilePath = getClass.getResource("/test_data.csv").getPath
    val fileReader: CsvReader = new CsvReaderImpl(testFilePath)
    val fileContent: List[String] = fileReader.readFile()

    assert(fileContent.size.equals(5))
  }
}
