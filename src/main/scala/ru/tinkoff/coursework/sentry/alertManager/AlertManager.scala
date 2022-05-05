package ru.tinkoff.coursework.sentry.alertManager

import ru.tinkoff.coursework.sentry.entities.UserEntity

class AlertManager {
  def alert(alertList: Set[UserEntity]): Unit = {
    println("AlertManager alerting users:")
    alertList.foreach(user => println(s"user: $user"))
  }

}