package ru.tinkoff.coursework.sentry.alertManager

import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
import ru.tinkoff.coursework.sentry.alertManager.telegramBot.SentryBot
import ru.tinkoff.coursework.sentry.database.{ServiceDAO, TagDAO, TelegramDAO, UserDAO}
import ru.tinkoff.coursework.sentry.entities.{FailureEntity, UserEntity}
import cats.implicits._
import ru.tinkoff.coursework.sentry.ServiceIdNotFoundException

import java.sql.Timestamp
import java.time.LocalDateTime

class AlertManagerImpl(serviceDAO: ServiceDAO,
                       tagDAO: TagDAO,
                       userDAO: UserDAO,
                       telegramDAO: TelegramDAO,
                       telegramBot: Option[SentryBot] = None) extends AlertManager with LazyLogging {

  override def alertSubscribers(failureEvent: FailureEntity): IO[List[Option[Int]]] = {
    for {
      serviceId <- serviceDAO.getServiceId(failureEvent.URL)
      alertList <- serviceId match {
        case Some(id) => getUsersByServiceId(id)
        case None => throw ServiceIdNotFoundException()
      }
      alertResult <- alert(alertList, failureEvent)
    } yield alertResult
  }

  private def getUsersByServiceId(serviceId: Long): IO[Set[UserEntity]] = {
    for {
      tagList <- tagDAO.getTagsByServiceId(serviceId)
      userJobList <- userDAO.getUsersDutyInJobs(Timestamp.valueOf(LocalDateTime.now()))
      userTagList <- userDAO.getUsersByTags(tagList)
      userServiceList <- userDAO.getUsersByServiceId(serviceId)
      userList = userJobList ++ userTagList ++ userServiceList
    } yield userList
  }

  private def alert(alertList: Set[UserEntity], failureEvent: FailureEntity): IO[List[Option[Int]]] = {
    logger.info("AlertManager alerting users:")
    logger.info("alert list {}", alertList)
    telegramBot match {
      case None => IO(List.empty)
      case Some(tgBot) =>
        alertList.map(userEntity =>
          telegramDAO.getChatByUserId(userEntity.userId).flatMap({
            case Some(chatId) =>
              logger.info(s"telegramBot.sendToChat chatId - $chatId, text - $failureEvent")
              tgBot.sendToChat(chatId, createMessage(userEntity,failureEvent)).map(
                m => Some(m.messageId))
            case _ => IO(None)
          })
        ).toList.sequence
    }
  }

  private def createMessage(userEntity: UserEntity,failureEntity: FailureEntity):String =
    s"${userEntity.username}, sentry registered failure at service ${failureEntity.URL}. " +
    s"Failed at ${failureEntity.timestamp}. Description: ${failureEntity.description}"
}