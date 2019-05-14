package com.lohika.vteraz.repository

case class IotDevice(id: Long, userId: Long, sn: String)

trait IotDeviceRepository [F[_]] {
    def registerDevice(userId: Long, serialNumber: String): F[IotDevice]

    def getById(id: Long): F[Option[IotDevice]]

    def getBySn(sn: String): F[Option[IotDevice]]

    def getByUser(userId: Long): F[Seq[IotDevice]]
}