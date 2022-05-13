package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.ServiceEntity
import ru.tinkoff.coursework.sentry.database.ServiceDAO


class ServiceServiceImpl(db: ServiceDAO) extends ServiceService {
  def createService(service: ServiceEntity): IO[Boolean] = db.createService(service)

  def assignUserToService(userId: Long, service: ServiceEntity): IO[Boolean] =
    db.assignUserToService(userId, service.serviceId)

  def findService(id: Long):IO[Option[ServiceEntity]] = db.findServiceById(id)

  def findServicesByTag(tag: String): IO[List[ServiceEntity]] = db.findServicesByTag(tag)
}
