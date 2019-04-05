package com.lohika.vteraz.service

import com.lohika.vteraz.data.CsvReader

/**
  * Service to process crimes
  */
object CrimeService {

  /**
    * Returns list of top 5 thefts grouped by crime location. Crimes that has no id, lon and lat will be dropped
    *
    * @param fileReader Instance of [[com.lohika.vteraz.data.CsvReader]] to get crimes list
    * @return List of crimes
    */
  def getTop5TheftLocations()(implicit fileReader: CsvReader): Seq[(String, List[CrimeDto])] = {
    parseCrimes(fileReader)
      .filter(crime => crime.id.nonEmpty)
      .filter(crime => crime.lat.nonEmpty)
      .filter(crime => crime.lon.nonEmpty)
      .filter(crime => crime.crimeType.contains("theft"))
      .groupBy(crime => s"(${crime.lat}, ${crime.lon})")
      .toSeq
      .sortBy(_._2.size)(Ordering[Int].reverse)
      .slice(0, 5)
  }

  private def parseCrimes(fileReader: CsvReader): List[CrimeDto] = {
    fileReader.readFile().map(line => line.split(","))
      .map(csvRow => CrimeDto(csvRow(0), csvRow(4), csvRow(5), csvRow(9), csvRow(6)))
  }

  case class CrimeDto(id: String, lon: String, lat: String, crimeType: String, location: String)

}
