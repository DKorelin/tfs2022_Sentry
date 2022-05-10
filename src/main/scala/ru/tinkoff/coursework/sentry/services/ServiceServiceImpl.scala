package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.ServiceEntity
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import java.util.UUID


class ServiceServiceImpl(db: SentryDatabase) extends ServiceService {
  def createService(service: ServiceEntity): IO[Boolean] = db.createService(service)

  def assignUserToService(userId: UUID, service: ServiceEntity): IO[Boolean] =
    db.assignUserToService(userId, service.serviceId)

  def findService(id: Long):IO[Option[ServiceEntity]] = db.findServiceById(id)
}
