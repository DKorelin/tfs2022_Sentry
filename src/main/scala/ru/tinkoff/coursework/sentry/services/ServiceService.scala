package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.ServiceEntity

trait ServiceService {
  def createService(service: ServiceEntity): IO[Boolean]

  def assignUserToService(userId: Long, service: ServiceEntity): IO[Boolean]

  def findService(id: Long):IO[Option[ServiceEntity]]

  def findServicesByTag(tag: String): IO[List[ServiceEntity]]
}
