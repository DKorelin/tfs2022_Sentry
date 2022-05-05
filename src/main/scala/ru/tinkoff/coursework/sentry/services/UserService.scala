package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import ru.tinkoff.coursework.sentry.entities.UserEntity

import java.util.UUID

class UserService(db: SentryDatabase) {

  def createUser(userEntity: UserEntity): IO[Int] = db.createUser(userEntity)

  def findUser(id: UUID): IO[UserEntity] = db.findUserById(id)

}
