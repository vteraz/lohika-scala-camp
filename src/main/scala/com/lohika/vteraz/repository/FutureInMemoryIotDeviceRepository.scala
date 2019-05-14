package com.lohika.vteraz.repository

import java.util.concurrent.atomic.AtomicLong

import scala.concurrent.Future

class FutureInMemoryIotDeviceRepository extends IotDeviceRepository[Future] {
    private var storage: Map[Long, IotDevice] = Map()
    private val idGenerator: AtomicLong = new AtomicLong()

    override def registerDevice(userId: Long, serialNumber: String): Future[IotDevice] = {
        Future.successful {
            val id = idGenerator.incrementAndGet()
            val device = IotDevice(id, userId, serialNumber)
            storage = storage + {
                id -> device
            }
            device
        }
    }

    override def getById(id: Long): Future[Option[IotDevice]] = {
        Future.successful(storage.values.find(d => d.id.equals(id)))
    }

    override def getBySn(sn: String): Future[Option[IotDevice]] = {
        Future.successful(storage.values.find(d => d.sn.equals(sn)))
    }

    override def getByUser(userId: Long): Future[Seq[IotDevice]] = {
        Future.successful(storage.values.filter(d => d.userId.equals(userId)).toSeq)
    }
}
