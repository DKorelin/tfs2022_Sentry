package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.database.UserDAO
import ru.tinkoff.coursework.sentry.entities.UserEntity

class UserServiceImpl(userDAO: UserDAO) extends UserService {
  def createUser(userEntity: UserEntity): IO[Boolean] = userDAO.createUser(userEntity)

  def findUser(id: Long): IO[Option[UserEntity]] = userDAO.findUserById(id)

  def findUsersByTag(tag: String): IO[List[UserEntity]] = userDAO.findUsersByTag(tag)

}
