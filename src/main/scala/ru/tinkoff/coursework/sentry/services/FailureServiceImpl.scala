package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.alertManager.AlertManager
import ru.tinkoff.coursework.sentry.entities.FailureEntity
import ru.tinkoff.coursework.sentry.database.FailureDAO

class FailureServiceImpl(failureDAO: FailureDAO, alertManager: AlertManager) extends FailureService {
  override def findFailure(id: Long):IO[Option[FailureEntity]] = failureDAO.findFailureById(id:Long)

  override def recordFailure(failureEvent: FailureEntity):IO[Long] = {
    for {
      writeResult <- failureDAO.createFailure(failureEvent)
      _ <- alertManager.alertSubscribers(failureEvent)
    } yield writeResult
  }
}
