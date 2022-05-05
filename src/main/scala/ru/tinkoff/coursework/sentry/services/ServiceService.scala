package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import ru.tinkoff.coursework.sentry.entities.ServiceEntity

import java.util.UUID

class ServiceService(db: SentryDatabase) {
  def tagUserToService(userId: UUID, service: ServiceEntity): IO[Boolean] =
    db.tagUserToService(userId, service.serviceId)

  def createService(service: ServiceEntity): IO[Int] = db.createService(service)

  def findService(id: Long): IO[ServiceEntity] = ???

}
