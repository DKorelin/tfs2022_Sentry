package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.entities.{ServiceEntity, TagEntity, UserEntity}

import java.util.UUID

trait TagService {
  def findUsersByTag(tag: String): IO[List[UserEntity]]
  def findServicesByTag(tag: String): IO[List[ServiceEntity]]

  def createUserTag(userId: UUID, tagEntity: TagEntity): IO[Boolean]

  def createServiceTag(serviceId: Long, tagEntity: TagEntity): IO[Boolean]
}
