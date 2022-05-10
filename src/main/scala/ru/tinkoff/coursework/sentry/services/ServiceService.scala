package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.ServiceEntity

import java.util.UUID

trait ServiceService {
  def createService(service: ServiceEntity): IO[Boolean]

  def assignUserToService(userId: UUID, service: ServiceEntity): IO[Boolean]

  def findService(id: Long):IO[Option[ServiceEntity]]
}
