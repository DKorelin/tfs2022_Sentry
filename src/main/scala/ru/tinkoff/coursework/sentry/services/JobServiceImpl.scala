package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.JobEntity
import ru.tinkoff.coursework.sentry.database.JobDAO

class JobServiceImpl(db: JobDAO) extends JobService {
  def findJob(id: Long): IO[Option[JobEntity]] = db.findJobById(id)

  def createJob(userId: Long, jobEntity: JobEntity): IO[Boolean] = db.createJob(userId, jobEntity)
}
