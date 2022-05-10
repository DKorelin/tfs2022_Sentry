package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.JobEntity
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import java.util.UUID

class JobServiceImpl(db: SentryDatabase) extends JobService {
  def findJob(id: Long): IO[Option[JobEntity]] = db.findJobById(id)

  def createJob(userId: UUID, jobEntity: JobEntity): IO[Boolean] = db.createJob(userId, jobEntity)
}
