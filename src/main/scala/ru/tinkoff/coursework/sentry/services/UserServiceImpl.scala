package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.database.UserDAO
import ru.tinkoff.coursework.sentry.entities.UserEntity

class UserServiceImpl(db: UserDAO) extends UserService {
  def createUser(userEntity: UserEntity): IO[Boolean] = db.createUser(userEntity)

  def findUser(id: Long):IO[Option[UserEntity]] = db.findUserById(id)

  def findUsersByTag(tag: String): IO[List[UserEntity]] = db.findUsersByTag(tag)

}
