package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import ru.tinkoff.coursework.sentry.entities.TagEntity

import java.util.UUID

class TagServiceImpl(db: SentryDatabase) extends TagService {
  def findTag(tag: String): IO[TagEntity] = ???

  def createUserTag(userId: UUID, tagEntity: TagEntity): IO[Boolean] = db.tagUser(userId, tagEntity.tag)

  def createServiceTag(serviceId: Long, tagEntity: TagEntity): IO[Boolean] = db.tagService(serviceId, tagEntity.tag)

}
