package ru.tinkoff.coursework.sentry.database

import cats.effect.IO

trait TagDAO {
  def createServiceTag(serviceId: Long, tag: String): IO[Boolean]

  def createUserTag(userId: Long, tag: String): IO[Boolean]

  def getTagsByServiceId(serviceId: Long): IO[Set[String]]
}
