package ru.tinkoff.coursework.sentry.alertManager

import ru.tinkoff.coursework.sentry.entities.{FailureEntity, UserEntity}

trait AlertManager {
  def alert(alertList: Set[UserEntity], failureEvent: FailureEntity):Unit
}
