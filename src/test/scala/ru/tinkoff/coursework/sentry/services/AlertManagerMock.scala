package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.alertManager.AlertManager
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import ru.tinkoff.coursework.sentry.entities.{FailureEntity, UserEntity}

import java.sql.Timestamp
import java.time.LocalDateTime
import scala.collection.mutable


class AlertManagerMock (db: SentryDatabase) extends AlertManager {
  var messageObjects: mutable.Set[(UserEntity, FailureEntity)] = scala.collection.mutable.Set[(UserEntity, FailureEntity)]()

  val mockUser: UserEntity = UserEntity(1, "mockAlertManagerUser", "mockAlertManagerMail", "mockAlertManagerNum")

  override def alertSubscribers(serviceId: Long, failureEvent: FailureEntity): IO[Unit] = for {
    alertList <- getUsersByServiceId(serviceId)
  } yield alert(alertList, failureEvent)

  def alert(alertList: Set[UserEntity], failureEvent: FailureEntity): Unit = {
    alertList match {
      case alertList if alertList.isEmpty => messageObjects.add(mockUser, failureEvent)
      case alertList => alertList.foreach(user => messageObjects.add((user, failureEvent)))
    }
  }

  private def getUsersByServiceId(serviceId: Long): IO[Set[UserEntity]] = {
    for {
      tagList <- db.getTagsByServiceId(serviceId)
      userJobList <- db.getUsersDutyInJobs(Timestamp.valueOf(LocalDateTime.now()))
      userTagList <- db.getUsersByTags(tagList)
      userServiceList <- db.getUsersByServiceId(serviceId)
      userList = userJobList ++ userTagList ++ userServiceList
    } yield userList
  }

  override def bind(sentryId: Long, chatId: Long): IO[Boolean] = ???
}
