package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.UserEntity

trait UserService {
  def createUser(userEntity: UserEntity): IO[Boolean]

  def findUsersByTag(tag: String): IO[List[UserEntity]]

  def findUser(id: Long): IO[Option[UserEntity]]
}
