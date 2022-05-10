package ru.tinkoff.coursework.sentry.services

import ru.tinkoff.coursework.sentry.alertManager.AlertManager
import ru.tinkoff.coursework.sentry.entities.{FailureEntity, UserEntity}

import java.util.UUID
import scala.collection.mutable


object AlertManagerMock extends AlertManager {
  var messageObjects: mutable.Set[(UserEntity, FailureEntity)] = scala.collection.mutable.Set[(UserEntity,FailureEntity)]()

  val mockUser: UserEntity = UserEntity(UUID.randomUUID(),"mockAlertManagerUser","mockAlertManagerMail","mockAlertManagerNum")

  override def alert(alertList: Set[UserEntity], failureEvent: FailureEntity): Unit = {
    alertList match {
      case alertList if alertList.isEmpty => messageObjects.add(mockUser,failureEvent)
      case alertList => alertList.foreach(user => messageObjects.add((user,failureEvent)))
    }

  }
}
