package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import ru.tinkoff.coursework.sentry.entities.ServiceEntity

import java.util.UUID

class ServiceServiceImpl(db: SentryDatabase) extends ServiceService {
  def findService(id: Long):IO[Option[ServiceEntity]] = db.findServiceById(id)

  def tagUserToService(userId: UUID, service: ServiceEntity): IO[Boolean] =
    db.tagUserToService(userId, service.serviceId)

  def createService(service: ServiceEntity): IO[Int] = db.createService(service)
}
