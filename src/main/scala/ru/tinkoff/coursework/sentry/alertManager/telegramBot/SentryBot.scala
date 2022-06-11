package ru.tinkoff.coursework.sentry.alertManager.telegramBot

import cats.effect.IO
import com.bot4s.telegram.api.declarative._
import com.bot4s.telegram.api.declarative.Commands
import com.bot4s.telegram.cats.Polling
import com.bot4s.telegram.methods.SendMessage
import com.bot4s.telegram.models._
import ru.tinkoff.coursework.sentry.UserIdNotFoundException
import ru.tinkoff.coursework.sentry.database.{ServiceDAO, TagDAO, TelegramDAO, UserDAO}
import ru.tinkoff.coursework.sentry.entities.UserEntity

import scala.util.Try

/**
 * @param token SentryBot's token.
 */
class SentryBot(token: String, serviceDAO: ServiceDAO,
                tagDAO: TagDAO,
                userDAO: UserDAO,
                telegramDAO: TelegramDAO)
  extends ExampleBot[IO](token)
    with Commands[IO]
    with Callbacks[IO]
    with Polling[IO] {

  // Extractor
  object Long {
    def unapply(s: String): Option[Long] = Try(s.toLong).toOption
  }

  onCommand("/assignAs ") { implicit msg =>
    withArgs {
      case Seq(Long(sentryId)) =>
        telegramDAO.bindUserWithTelegramChat(sentryId, msg.chat.id)
          .flatMap(wrRes => reply(s"you are successfully ($wrRes) assigned!").void)
      case _ =>
        reply("Invalid argument. Usage: /assignAs 123").void
    }
  }

  onCommand("/registerAs ") { implicit msg =>
    withArgs {
      case Seq(username) =>
        userDAO.createUser(UserEntity(msg.chat.id, username, "-", "-")).flatMap(_ =>
          telegramDAO.bindUserWithTelegramChat(msg.chat.id, msg.chat.id)
            .flatMap(wrRes => reply(s"you are successfully ($wrRes) registered!").void)
        )
      case _ =>
        reply("Invalid usage. Usage: /registerAs <username>").void
    }
  }

  onCommand("/subscribeURL ") { implicit msg =>
    withArgs {
      case Seq(url) =>
        serviceDAO.getServiceId(url).flatMap({
          case Some(serviceId) => telegramDAO.getUserIdByChat(msg.chat.id).flatMap({
            case Some(userId) => serviceDAO.assignUserToService(userId, serviceId).flatMap(userIsAssignedToService =>
              reply(s"userIsAssignedToService result - $userIsAssignedToService!").void)
            case None => throw UserIdNotFoundException()
          case None => reply("no such url tracking").void
          })
        })
      case _ =>
        reply("Invalid usage. Usage: /subscribeURL <url>").void
    }
  }

  onCommand("/registerFailure ") { implicit msg =>
    withArgs {
      case Seq(url) =>
        serviceDAO.getServiceId(url).flatMap({
          case Some(serviceId) => telegramDAO.getUserIdByChat(msg.chat.id).flatMap({
            case Some(userId) => serviceDAO.assignUserToService(userId, serviceId).flatMap(userIsAssignedToService =>
              reply(s"userIsAssignedToService result - $userIsAssignedToService!").void)
            case None => throw UserIdNotFoundException()
            case None => reply("no such url tracking").void
          })
        })
      case _ =>
        reply("Invalid usage. Usage: /subscribeURL <url>").void
    }
  }

  onCommand("/getChatByUserId ") { implicit msg =>
    withArgs {
      case Seq(Long(sentryId)) =>
        telegramDAO.getChatByUserId(sentryId)
          .flatMap(chatId => reply(s"user $sentryId chatId is $chatId").void)
      case _ =>
        reply("Invalid argument. Usage: /getChatByUserId 123").void
    }
  }

  onCommand("/testHello") { implicit msg =>
    logger.info(s"entered testHello")
    reply("Hello tinkoff fintech school!").void
  }

  def sendToChat(chatId: ChatId, text: String): IO[Message] = {
    logger.info(s"entered sendToChat with chatId: $chatId, text: $text")
    request(SendMessage(chatId, text))
  }
}