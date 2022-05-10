package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.alertManager.AlertManager
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import ru.tinkoff.coursework.sentry.entities.{FailureEntity, UserEntity}

import java.sql.Timestamp
import java.time.LocalDateTime

class FailureServiceImpl(db: SentryDatabase, alertManager: AlertManager) extends FailureService {
  def findFailure(id: Long):IO[Option[FailureEntity]] = db.findFailureById(id:Long)


  def recordFailure(failureEvent: FailureEntity):IO[Int] = {
    for {
      writeResult <- db.writeFailure(failureEvent)
      serviceId <- db.getServiceId(failureEvent.URL)
      _ <- alertSubscribers(serviceId, failureEvent)
    } yield writeResult

  }

  private def alertSubscribers(serviceId: Long, failureEvent: FailureEntity): IO[Unit] = {
    for {
      alertList <- getUsersByServiceId(serviceId)
    } yield alertManager.alert(alertList, failureEvent)
  }

  private def getUsersByServiceId(serviceId: Long): IO[Set[UserEntity]] = {
    for {
      tagList <- db.getTagsIdByServiceId(serviceId)
      userJobList <- db.getUsersIdDutyInJobs(Timestamp.valueOf(LocalDateTime.now()))
      userTagList <- db.getUsersIdByTagsId(tagList)
      userServiceList <- db.getUsersIdByServiceId(serviceId)
      userIdList = userJobList ++ userTagList ++ userServiceList
      userList <- db.getUsersById(userIdList)
    } yield userList
  }
}
