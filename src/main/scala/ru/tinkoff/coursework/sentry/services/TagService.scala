package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.TagEntity

import java.util.UUID

trait TagService {
  def findTag(tag: String): IO[TagEntity]

  def createUserTag(userId: UUID, tagEntity: TagEntity): IO[Boolean]

  def createServiceTag(serviceId: Long, tagEntity: TagEntity): IO[Boolean]
}
