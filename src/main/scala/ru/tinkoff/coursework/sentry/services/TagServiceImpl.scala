package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.database.TagDAO
import ru.tinkoff.coursework.sentry.entities.TagEntity

class TagServiceImpl(db: TagDAO) extends TagService {

  def createUserTag(userId: Long, tagEntity: TagEntity): IO[Boolean] = db.createUserTag(userId, tagEntity.tag)

  def createServiceTag(serviceId: Long, tagEntity: TagEntity): IO[Boolean] = db.createServiceTag(serviceId, tagEntity.tag)

}
