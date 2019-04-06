package com.lohika.vteraz

import com.lohika.vteraz.data.{CsvReader, CsvReaderImpl}
import com.lohika.vteraz.service.CrimeService
import com.lohika.vteraz.service.CrimeService.CrimeDto
import org.scalatest._
import org.scalatest.Matchers._

class CrimeServiceTest extends FunSuite with DiagrammedAssertions {

  test("Test get top 5 theft locations") {
    val testFilePath = getClass.getResource("/test_data.csv").getPath
    implicit val fileReader: CsvReader = new CsvReaderImpl(testFilePath)

    val data: Seq[(String, List[CrimeDto])] = CrimeService.getTop5TheftLocations()

    val crimesList: List[CrimeDto] = data.head._2

    assert(data.size.equals(5), "List should contain 5 locations")
    assert(crimesList.size > data(1)._2.size, "List should be sorted by crimes incidents desc")
    assert(crimesList.size.equals(7), "Invalid crimes list size")
    assert(data(1)._2.size.equals(5), "Invalid crimes list size")

    crimesList.foreach(crime => {
      crime.id.isEmpty shouldBe false
      crime.location.isEmpty shouldBe false
      crime.lon.isEmpty shouldBe false
      crime.lat.isEmpty shouldBe false
    })
  }


}
