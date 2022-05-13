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
import ru.tinkoff.coursework.sentry.entities.FailureEntity

class FailureDAOImpl(xa: Aux[IO, Unit]) extends FailureDAO {
  override def createFailure(failure: FailureEntity): IO[Boolean] = {
    sql"""
      INSERT INTO failureTable (failureId,URL,description,failureTime)
      VALUES (${failure.failureId},${failure.URL},${failure.description},${failure.timestamp})"""
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }

  override def findFailureById(id: Long): IO[Option[FailureEntity]] = {
    sql"""SELECT failureTable.failureId, failureTable.URL, failureTable.description, failureTable.failureTime
      FROM failureTable WHERE failureTable.failureId = $id"""
      .query[FailureEntity]
      .option
      .transact(xa)
  }
}
