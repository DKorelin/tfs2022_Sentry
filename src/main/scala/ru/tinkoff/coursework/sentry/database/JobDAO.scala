package ru.tinkoff.coursework.sentry.database

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.JobEntity

trait JobDAO {
  def createJob(userId: Long, jobEntity: JobEntity): IO[Boolean]

  def findJobById(id: Long): IO[Option[JobEntity]]
}
