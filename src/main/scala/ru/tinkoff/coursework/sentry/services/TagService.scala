package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.TagEntity

trait TagService {
  def createUserTag(userId: Long, tagEntity: TagEntity): IO[Boolean]

  def createServiceTag(serviceId: Long, tagEntity: TagEntity): IO[Boolean]
}
