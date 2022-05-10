package ru.tinkoff.coursework.sentry.database

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.{FailureEntity, JobEntity, ServiceEntity, UserEntity}
import java.sql.Timestamp
import java.util.UUID

trait SentryDatabase {
  def createJob(userId: UUID, jobEntity: JobEntity): IO[Boolean]

  def createService(service: ServiceEntity): IO[Boolean]

  def createUser(user: UserEntity): IO[Boolean]

  def createFailure(failure: FailureEntity): IO[Boolean]

  def createServiceTag(serviceId: Long, tag: String): IO[Boolean]

  def createUserTag(userId: UUID, tag: String): IO[Boolean]

  def assignUserToService(userId: UUID, serviceId: Long): IO[Boolean]

  def findServiceById(id: Long): IO[Option[ServiceEntity]]

  def findFailureById(id: Long): IO[Option[FailureEntity]]

  def findJobById(id: Long): IO[Option[JobEntity]]

  def findUserById(userId: UUID): IO[Option[UserEntity]]

  def findServicesByTag(tag: String): IO[List[ServiceEntity]]

  def findUsersByTag(tag: String): IO[List[UserEntity]]

  def getServiceId(URL: String): IO[Option[Long]]

  def getTagsByServiceId(serviceId: Long): IO[Set[String]]

  def getUsersByServiceId(serviceId: Long): IO[Set[UserEntity]]

  def getUsersDutyInJobs(currentTime: Timestamp): IO[Set[UserEntity]]

  def getUsersByTags(tagList: Set[String]): IO[Set[UserEntity]]
}
