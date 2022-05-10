package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import ru.tinkoff.coursework.sentry.entities.UserEntity

import java.util.UUID

class UserServiceImpl(db: SentryDatabase) extends UserService {
  def createUser(userEntity: UserEntity): IO[Int] = db.createUser(userEntity)

  def findUser(id: UUID):IO[Option[UserEntity]] = db.findUserById(id)
}
