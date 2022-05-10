package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.JobEntity

import java.util.UUID

trait JobService {
  def findJob(id: Long): IO[Option[JobEntity]]

  def createJob(userId: UUID, jobEntity: JobEntity): IO[Boolean]
}
