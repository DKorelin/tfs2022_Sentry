package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import ru.tinkoff.coursework.sentry.entities.{ServiceEntity, TagEntity, UserEntity}

import java.util.UUID

class TagServiceImpl(db: SentryDatabase) extends TagService {
  def findUsersByTag(tag: String): IO[List[UserEntity]] = db.findUsersByTag(tag)
  def findServicesByTag(tag: String): IO[List[ServiceEntity]] = db.findServicesByTag(tag)

  def createUserTag(userId: UUID, tagEntity: TagEntity): IO[Boolean] = db.createUserTag(userId, tagEntity.tag)

  def createServiceTag(serviceId: Long, tagEntity: TagEntity): IO[Boolean] = db.createServiceTag(serviceId, tagEntity.tag)

}
