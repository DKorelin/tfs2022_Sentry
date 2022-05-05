package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.alertManager.AlertManager
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import ru.tinkoff.coursework.sentry.entities.{FailureEntity, UserEntity}

class FailureService(db: SentryDatabase, alertManager: AlertManager) {

  def find(id: Long): IO[FailureEntity] = ???

  def recordFailure(failureEvent: FailureEntity) = {
    for {
      writeResult <- db.writeFailure(failureEvent)
      serviceId <- db.getServiceId(failureEvent.URL)
      _ <- alertSubscribers(serviceId)
    } yield writeResult

  }

  private def alertSubscribers(serviceId: Long) = {
    for {
      alertList <- getUsersByServiceId(serviceId)
    } yield alertManager.alert(alertList)
  }

  private def getUsersByServiceId(serviceId: Long): IO[Set[UserEntity]] = {
    for {
      tagList <- db.getTagsIdByServiceId(serviceId)
      jobList <- db.getJobsIdByTagsId(tagList) //Pass Time ??
      userJobList <- db.getUsersIdByJobId(jobList)
      userTagList <- db.getUsersIdByTagsId(tagList)
      userServiceList <- db.getUsersIdByServiceId(serviceId)
      userIdList = userJobList ++ userTagList ++ userServiceList
      userList <- db.getUsersById(userIdList)
    } yield userList
  }
}
