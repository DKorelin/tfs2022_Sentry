package ru.tinkoff.coursework.sentry.alertManager

import ru.tinkoff.coursework.sentry.entities.{FailureEntity, UserEntity}

class AlertManagerImpl extends AlertManager {
  def alert(alertList: Set[UserEntity], failureEvent: FailureEntity): Unit = {
    println("AlertManager alerting users:")
    alertList.foreach(user => println(s"user: $user. failure: $failureEvent"))
  }

}