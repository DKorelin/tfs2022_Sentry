package ru.tinkoff.coursework.sentry.database

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.FailureEntity

trait FailureDAO {
  def createFailure(failure: FailureEntity): IO[Long]

  def findFailureById(id: Long): IO[Option[FailureEntity]]
}
