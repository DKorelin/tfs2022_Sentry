package ru.tinkoff.coursework.sentry.alertManager

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.FailureEntity

trait AlertManager {
  def alertSubscribers(failureEvent: FailureEntity): IO[List[Option[Int]]]
}
