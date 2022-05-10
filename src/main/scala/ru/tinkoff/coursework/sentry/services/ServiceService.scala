package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.ServiceEntity

import java.util.UUID

trait ServiceService {
  def findService(id: Long):IO[Option[ServiceEntity]]

  def tagUserToService(userId: UUID, service: ServiceEntity): IO[Boolean]

  def createService(service: ServiceEntity): IO[Int]
}
