package ru.tinkoff.coursework.sentry.database

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.UserEntity
import java.sql.Timestamp

trait UserDAO {
  def findUserById(id: Long): IO[Option[UserEntity]]

  def findUsersByTag(tag: String): IO[List[UserEntity]]

  def createUser(userEntity: UserEntity): IO[Boolean]

  def getUsersDutyInJobs(currentTime: Timestamp): IO[Set[UserEntity]]

  def getUsersByServiceId(serviceId: Long): IO[Set[UserEntity]]

  def getUsersByTags(tagList: Set[String]): IO[Set[UserEntity]]
}
