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
import ru.tinkoff.coursework.sentry.entities.UserEntity

class TagDAOImpl(xa: Aux[IO, Unit]) extends TagDAO {
  override def createServiceTag(serviceId: Long, tag: String): IO[Boolean] = {
    sql"""
      INSERT INTO serviceTagTable (serviceId,tag)
      VALUES ($serviceId,$tag)"""
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }

  override def createUserTag(userId: Long, tag: String): IO[Boolean] = {
    sql"""
      INSERT INTO userTagTable (userId,tag)
      VALUES ($userId,$tag)"""
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }

  override def getTagsByServiceId(serviceId: Long): IO[Set[String]] = {
    sql"""
         SELECT serviceTagTable.tag
         FROM serviceTagTable WHERE serviceTagTable.serviceId = $serviceId """
      .query[String]
      .to[Set]
      .transact(xa)
  }
}
