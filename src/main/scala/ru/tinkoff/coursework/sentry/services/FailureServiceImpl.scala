package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.ServiceIdNotFoundException
import ru.tinkoff.coursework.sentry.alertManager.AlertManager
import ru.tinkoff.coursework.sentry.entities.{FailureEntity, UserEntity}
import ru.tinkoff.coursework.sentry.database.SentryDatabase

import java.sql.Timestamp
import java.time.LocalDateTime


class FailureServiceImpl(db: SentryDatabase, alertManager: AlertManager) extends FailureService {
  def findFailure(id: Long):IO[Option[FailureEntity]] = db.findFailureById(id:Long)


  def recordFailure(failureEvent: FailureEntity):IO[Boolean] = {
    for {
      writeResult <- db.createFailure(failureEvent)
      serviceId <- db.getServiceId(failureEvent.URL)
      _ <- serviceId match {
        case Some(serviceId) => alertManager.alertSubscribers(serviceId, failureEvent)
        case None => throw ServiceIdNotFoundException()
      }
    } yield writeResult

  }
}
