package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.JobEntity

trait JobService {
  def findJob(id: Long): IO[Option[JobEntity]]

  def createJob(userId: Long, jobEntity: JobEntity): IO[Boolean]
}
