package ru.tinkoff.coursework.sentry.alertManager

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.FailureEntity

trait AlertManager {
  def bind(sentryId: Long, chatId: Long):IO[Boolean]

  def alertSubscribers(serviceId: Long, failureEvent: FailureEntity): IO[Unit]
}
