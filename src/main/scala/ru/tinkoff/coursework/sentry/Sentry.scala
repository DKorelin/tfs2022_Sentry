package ru.tinkoff.coursework.sentry

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.{Hostname, IpLiteralSyntax}
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.Router
import ru.tinkoff.coursework.sentry.alertManager.{AlertManager, AlertManagerImpl}
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import ru.tinkoff.coursework.sentry.endpoints.{FailureApi, JobsApi, UserApi}
import ru.tinkoff.coursework.sentry.services.{FailureServiceImpl, JobServiceImpl, ServiceServiceImpl}

object Sentry extends IOApp {
  val database = new SentryDatabase
  val alertManager: AlertManager = new AlertManagerImpl()
  val failureService = new FailureServiceImpl(database, alertManager)
  val failureApi: HttpRoutes[IO] = new FailureApi(failureService).failureRoutes
  val services: HttpRoutes[IO] = failureApi
  val httpApp: Kleisli[IO, Request[IO], Response[IO]] = Router("/api" -> services).orNotFound

  def run(args: List[String]): IO[ExitCode] = {
    val server = EmberServerBuilder
      .default[IO]
      .withHost(Hostname.fromString("localhost").get)
      .withPort(port"8080")
      .withHttpApp(httpApp)
      .withErrorHandler {
        case e => IO(e.printStackTrace()).as(org.http4s.Response.timeout[IO])
      }
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
    for {
      _ <- database.failureScheme
      s <- server
    } yield s
  }
}