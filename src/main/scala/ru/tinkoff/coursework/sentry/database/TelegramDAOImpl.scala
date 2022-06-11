package ru.tinkoff.coursework.sentry.database
import cats._
import cats.data._
import cats.effect._
import doobie._
import cats.implicits._
import doobie.implicits._
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.implicits.javasql._
import doobie.util.transactor.Transactor.Aux

class TelegramDAOImpl(xa: Aux[IO, Unit]) extends TelegramDAO {
  override def getChatByUserId(userId: Long): IO[Option[Long]] = {
    sql"""SELECT userWithTelegramChatTable.chatId
       FROM userWithTelegramChatTable
       WHERE userWithTelegramChatTable.sentryId = $userId """
      .query[Long]
      .option
      .transact(xa)
  }

  override def getUserIdByChat(chatId: Long): IO[Option[Long]] = {
    sql"""SELECT userWithTelegramChatTable.sentryId
       FROM userWithTelegramChatTable
       WHERE userWithTelegramChatTable.chatId = $chatId"""
      .query[Long]
      .option
      .transact(xa)
  }

  override def bindUserWithTelegramChat(sentryId: Long, chatId: Long): IO[Boolean] = {
    sql"""
      INSERT INTO userWithTelegramChatTable (sentryId, chatId)
      VALUES ($sentryId, $chatId)"""
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }
}
