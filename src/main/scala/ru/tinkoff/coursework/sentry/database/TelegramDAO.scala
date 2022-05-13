package ru.tinkoff.coursework.sentry.database

import cats.effect.IO

trait TelegramDAO {
  def getChatByUserId(userId: Long):IO[Long]

  def bindUserWithTelegramChat(sentryId: Long, chatId: Long): IO[Boolean]
}
