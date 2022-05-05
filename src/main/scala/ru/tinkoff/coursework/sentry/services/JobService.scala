package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.JobEntity
import java.util.UUID

class JobService {
  def createJob(userId: UUID, jobEntity: JobEntity): IO[Long] = ???

  def findJob(id: Long): IO[JobEntity] = ???


}
