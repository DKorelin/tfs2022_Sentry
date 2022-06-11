package ru.tinkoff.coursework.sentry.database

import cats.effect.IO

trait TelegramDAO {
  def getChatByUserId(userId: Long): IO[Option[Long]]

  def getUserIdByChat(chatId: Long): IO[Option[Long]]

  def bindUserWithTelegramChat(sentryId: Long, chatId: Long): IO[Boolean]
}
