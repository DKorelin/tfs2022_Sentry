package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.alertManager.AlertManager
import ru.tinkoff.coursework.sentry.entities.{FailureEntity, UserEntity}

import java.sql.Timestamp
import java.time.LocalDateTime
import scala.collection.mutable


class AlertManagerMock extends AlertManager {
  var messageObjects: mutable.Set[(UserEntity, FailureEntity)] = scala.collection.mutable.Set[(UserEntity, FailureEntity)]()
  val db: DatabaseMock.type = DatabaseMock
  val mockUser: UserEntity = UserEntity(1, "mockAlertManagerUser", "mockAlertManagerMail", "mockAlertManagerNum")

  override def alertSubscribers(failureEvent: FailureEntity): IO[List[Option[Int]]] = {
    db.getServiceId(failureEvent.URL)
      .flatMap(serviceId =>
        getUsersByServiceId(serviceId.get)
          .flatMap(alertList => alert(alertList, failureEvent))
      )
  }

  def alert(alertList: Set[UserEntity], failureEvent: FailureEntity): IO[List[Option[Int]]] = {
    alertList match {
      case alertList if alertList.isEmpty => messageObjects.add(mockUser, failureEvent)
      case alertList => alertList.foreach(user => messageObjects.add((user, failureEvent)))
    }
    val fake: List[Option[Int]] = List.empty
    IO(fake)
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
}
