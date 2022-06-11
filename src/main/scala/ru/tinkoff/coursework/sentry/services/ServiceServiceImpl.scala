package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.ServiceEntity
import ru.tinkoff.coursework.sentry.database.ServiceDAO

class ServiceServiceImpl(serviceDAO: ServiceDAO) extends ServiceService {
  def createService(service: ServiceEntity): IO[Boolean] = serviceDAO.createService(service)

  def assignUserToService(userId: Long, service: ServiceEntity): IO[Boolean] =
    serviceDAO.assignUserToService(userId, service.serviceId)

  def findService(id: Long): IO[Option[ServiceEntity]] = serviceDAO.findServiceById(id)

  def findServicesByTag(tag: String): IO[List[ServiceEntity]] = serviceDAO.findServicesByTag(tag)
}
