package ru.tinkoff.coursework.sentry.database

import cats._
import cats.data._
import cats.effect._
import doobie._
import cats.implicits._
import doobie.implicits._
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.implicits.javasql._
import doobie.util.transactor.Transactor.Aux
import ru.tinkoff.coursework.sentry.entities.ServiceEntity

class ServiceDAOImpl(xa: Aux[IO, Unit]) extends ServiceDAO {
  override def findServiceById(id: Long): IO[Option[ServiceEntity]] = {
    sql"""SELECT serviceTable.serviceId, serviceTable.URL
      FROM serviceTable WHERE serviceTable.serviceId = $id"""
      .query[ServiceEntity]
      .option
      .transact(xa)
  }

  override def assignUserToService(userId: Long, serviceId: Long): IO[Boolean] = {
    sql"""
      INSERT INTO serviceUserTable (userId,serviceId)
      VALUES ($userId,$serviceId)"""
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }

  override def createService(service: ServiceEntity): IO[Boolean] = {
    sql"""
      INSERT INTO serviceTable (serviceId,URL)
      VALUES (${service.serviceId},${service.URL})"""
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }

  override def findServicesByTag(tag: String): IO[List[ServiceEntity]] = {
    sql"""SELECT serviceTable.serviceId, serviceTable.URL
      FROM serviceTable JOIN serviceTagTable ON serviceTable.serviceId = serviceTagTable.serviceId
      WHERE serviceTagTable.tag = $tag"""
      .query[ServiceEntity]
      .to[List]
      .transact(xa)
  }

  override def getServiceId(URL: String): IO[Option[Long]] = {
    sql"""SELECT serviceTable.serviceId
      FROM serviceTable WHERE serviceTable.URL = $URL"""
      .query[Long]
      .option
      .transact(xa)
  }
}
