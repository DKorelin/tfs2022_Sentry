package ru.tinkoff.coursework.sentry.database

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.ServiceEntity

trait ServiceDAO {
  def findServiceById(id: Long): IO[Option[ServiceEntity]]

  def findServicesByTag(tag: String): IO[List[ServiceEntity]]

  def getServiceId(URL: String): IO[Option[Long]]

  def assignUserToService(userId: Long, serviceId: Long): IO[Boolean]

  def createService(service: ServiceEntity): IO[Boolean]

}
