package ru.tinkoff.coursework.sentry

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.{catsSyntaxTuple2Parallel, toSemigroupKOps}
import com.comcast.ip4s.{Hostname, Port}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import doobie.Transactor
import doobie.util.transactor.Transactor.Aux
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.Router
import ru.tinkoff.coursework.sentry.alertManager.telegramBot.SentryBot
import ru.tinkoff.coursework.sentry.alertManager.{AlertManager, AlertManagerImpl}
import ru.tinkoff.coursework.sentry.database.{FailureDAOImpl, JobDAOImpl, Schemes, ServiceDAOImpl, TagDAOImpl, TelegramDAOImpl, UserDAOImpl}
import ru.tinkoff.coursework.sentry.endpoints._
import ru.tinkoff.coursework.sentry.services._


object Sentry extends IOApp with LazyLogging {
  val config: Config = ConfigFactory.load("application.conf")
  val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    config.getString("sentry.databaseDriver"),
    config.getString("sentry.databaseUrl"),
    config.getString("sentry.databaseUser"),
    config.getString("sentry.databasePassword"))
  val database: Schemes = new Schemes(xa)
  val failureDAO: FailureDAOImpl = new FailureDAOImpl(xa)
  val jobDAO: JobDAOImpl = new JobDAOImpl(xa)
  val serviceDAO: ServiceDAOImpl = new ServiceDAOImpl(xa)
  val tagDAO: TagDAOImpl = new TagDAOImpl(xa)
  val telegramDAO: TelegramDAOImpl = new TelegramDAOImpl(xa)
  val userDAO: UserDAOImpl = new UserDAOImpl(xa)
  val telegramBot: SentryBot = new SentryBot(config.getString("sentry.sentryTelegramToken"), serviceDAO, tagDAO, userDAO, telegramDAO)
  val alertManager: AlertManager = new AlertManagerImpl(serviceDAO, tagDAO, userDAO, telegramDAO, Some(telegramBot))
  val failureService: FailureServiceImpl = new FailureServiceImpl(failureDAO, alertManager)
  val jobService: JobServiceImpl = new JobServiceImpl(jobDAO)
  val serviceService: ServiceServiceImpl = new ServiceServiceImpl(serviceDAO)
  val tagService: TagServiceImpl = new TagServiceImpl(tagDAO)
  val userService: UserServiceImpl = new UserServiceImpl(userDAO)

  val failureApi: HttpRoutes[IO] = new FailureApi(failureService).failureRoutes
  val jobApi: HttpRoutes[IO] = new JobApi(jobService).jobRoutes
  val serviceApi: HttpRoutes[IO] = new ServiceApi(serviceService).serviceRoutes
  val tagApi: HttpRoutes[IO] = new TagApi(tagService).tagRoutes
  val userApi: HttpRoutes[IO] = new UserApi(userService).userRoutes
  val services: HttpRoutes[IO] = failureApi <+> jobApi <+> serviceApi <+> tagApi <+> userApi
  val httpApp: Kleisli[IO, Request[IO], Response[IO]] = Router("/api" -> services).orNotFound

  def run(args: List[String]): IO[ExitCode] = {
    val serverStart: IO[ExitCode] = EmberServerBuilder
      .default[IO]
      .withHost(Hostname.fromString(config.getString("sentry.httpServerAddress")).get)
      .withPort(Port.fromString(config.getString("sentry.httpServerPort")).get)
      .withHttpApp(httpApp)
      .withErrorHandler {
        case e => IO(e.printStackTrace()).as(org.http4s.Response.timeout[IO])
      }
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
    val telegramBotStart = telegramBot.run().map(_ => ExitCode.Success)

    for {
      services <- (serverStart, telegramBotStart).parTupled
    } yield {
      services match {
        case (server, telegramBot) if server == ExitCode.Success && telegramBot == ExitCode.Success => ExitCode.Success
        case _ => ExitCode.Error
      }
    }
  }
}