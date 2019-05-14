package com.lohika.vteraz.repository

import java.util.concurrent.atomic.AtomicLong

import cats.Id

class IdInMemoryIotDeviceRepository extends IotDeviceRepository[Id] {
    private var storage: Map[Long, IotDevice] = Map()
    private val idGenerator: AtomicLong = new AtomicLong()

    override def registerDevice(userId: Long, serialNumber: String): Id[IotDevice] = {
        val id = idGenerator.incrementAndGet()
        val device = IotDevice(id, userId, serialNumber)
        storage = storage + {
            id -> device
        }
        device
    }

    override def getById(id: Long): Id[Option[IotDevice]] = {
        storage.values.find(d => d.id.equals(id))
    }

    override def getBySn(sn: String): Id[Option[IotDevice]] = {
        storage.values.find(d => d.sn.equals(sn))
    }

    override def getByUser(userId: Long): Id[Seq[IotDevice]] = {
        storage.values.filter(d => d.userId.equals(userId)).toSeq
    }
}

