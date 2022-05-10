package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.FailureEntity

trait FailureService {
  def findFailure(id: Long):IO[Option[FailureEntity]]

  def recordFailure(failureEvent: FailureEntity):IO[Boolean]
}
