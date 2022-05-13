package ru.tinkoff.coursework.sentry.alertManager.telegramBot
import cats.Applicative
import cats.effect.{Async, IO, Temporal}
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.bot4s.telegram.models.Message
import com.bot4s.telegram.api.declarative._
import com.bot4s.telegram.api.declarative.CommandFilterMagnet._
import com.bot4s.telegram.api.declarative.{Commands, RegexCommands}
import com.bot4s.telegram.cats.Polling
import com.bot4s.telegram.methods.SendMessage
import org.http4s.dsl.request
import ru.tinkoff.coursework.sentry.alertManager.AlertManager
import com.bot4s.telegram.methods._
import com.bot4s.telegram.models._

import scala.concurrent.duration._
import scala.util.Try

/**
 * Showcases different ways to declare commands (Commands + RegexCommands).
 *
 * Note that non-ASCII commands are not clickable.
 *
 * @param token Bot's token.
 */
class SentryBot[F[_]: Async: Temporal](token: String,am : AlertManager)
  extends ExampleBot[F](token)
    with Polling[F]
    with Commands[F]
    with RegexCommands[F] {

  // Extractor
  object Long {
    def unapply(s: String): Option[Long] = Try(s.toLong).toOption
  }

  onCommand("/assignAs ") { implicit msg =>
    withArgs {
      case Seq(Long(sentryId)) =>
        am.bind(sentryId,msg.chat.id)
        reply("you are successfully assigned!").void
      // Conveniently avoid MatchError, providing hints on usage.
      case _ =>
        reply("Invalid argument. Usage: /assignAs 123").void
    }
  }

  def sendToChat(chatId: ChatId,text:String): IO[Unit] = {
    for{
      _ <- IO{println("bob")}
      //_ <- request(SendMessage(chatId,text))
      //_ <- println("AlertManager alerting users:")
    }  yield ()
  }
}