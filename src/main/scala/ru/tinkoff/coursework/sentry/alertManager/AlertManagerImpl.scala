package ru.tinkoff.coursework.sentry.alertManager

import cats.effect.IO
import ru.tinkoff.coursework.sentry.alertManager.telegramBot.SentryBot
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import ru.tinkoff.coursework.sentry.entities.{FailureEntity, UserEntity}

import java.sql.Timestamp
import java.time.LocalDateTime

class AlertManagerImpl(db: SentryDatabase) extends AlertManager {
  val tokenMock = "this is a token mock"
  //private val telegramBot = new SentryBot[IO](tokenMock,this)
  override def alertSubscribers(serviceId: Long, failureEvent: FailureEntity): IO[Unit] = {
    for {
      alertList <- getUsersByServiceId(serviceId)
    } yield alert(alertList, failureEvent)
  }

  override def bind(sentryId: Long, chatId: Long): IO[Boolean] = db.bindUserWithTelegramChat(sentryId,chatId)

  private def getUsersByServiceId(serviceId: Long): IO[Set[UserEntity]] = {
    for {
      tagList <- db.getTagsByServiceId(serviceId)
      userJobList <- db.getUsersDutyInJobs(Timestamp.valueOf(LocalDateTime.now()))
      userTagList <- db.getUsersByTags(tagList)
      userServiceList <- db.getUsersByServiceId(serviceId)
      userList = userJobList ++ userTagList ++ userServiceList
    } yield userList
  }

  private def alert(alertList: Set[UserEntity], failureEvent: FailureEntity): Unit = {
    println("AlertManager alerting users:")
    alertList.foreach(userEntity => for {
      chatId <- db.getChatByUserId(userEntity.userId)
      //_ <- telegramBot.sendToChat(chatId,failureEvent.toString)
    } yield ())
    alertList.foreach(user => println(s"user: $user. failure: $failureEvent"))
  }


}