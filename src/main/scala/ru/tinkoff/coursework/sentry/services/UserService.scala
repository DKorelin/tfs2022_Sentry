package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.UserEntity

import java.util.UUID

trait UserService {
  def createUser(userEntity: UserEntity): IO[Boolean]

  def findUser(id: UUID): IO[Option[UserEntity]]
}
