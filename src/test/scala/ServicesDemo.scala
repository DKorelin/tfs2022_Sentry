import cats.effect.{ExitCode, IO, IOApp}
import ru.tinkoff.coursework.sentry.alertManager.AlertManager
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import ru.tinkoff.coursework.sentry.entities.{FailureEntity, ServiceEntity, UserEntity}
import ru.tinkoff.coursework.sentry.services.{FailureService, ServiceService, UserService}

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID

object ServicesDemo extends IOApp {
  val database = new SentryDatabase
  val schemes: IO[Unit] = for {
    _ <- database.userScheme
    _ <- database.userTagScheme
    _ <- database.serviceScheme
    _ <- database.serviceTagScheme
    _ <- database.failureScheme
    _ <- database.jobScheme
    _ <- database.jobUserSubscribeScheme
    _ <- database.serviceUserSubscribeScheme
  } yield ()

  val alertManager: AlertManager = new AlertManager
  val failureService = new FailureService(database, alertManager)
  val userService = new UserService(database)
  val serviceService = new ServiceService(database)

  override def run(args: List[String]): IO[ExitCode] = {
    val demoUUID = UUID.randomUUID()
    val demoUser = UserEntity(demoUUID, "bob", "dummy@mail.com", "8-800-555-35-35")
    val demoURL = "www.dummy.com"
    val demoService = ServiceEntity(1, demoURL)
    val demoFailure = FailureEntity(1, demoURL, "epic fail hacker is n00b1e", Timestamp.valueOf(LocalDateTime.now()))
    for {
      _ <- schemes
      _ <- userService.createUser(demoUser)
      _ <- serviceService.createService(demoService)
      _ <- serviceService.tagUserToService(demoUUID, demoService)
      _ <- failureService.recordFailure(demoFailure)
      //expecting user print caused by alertManager
    } yield ExitCode.Success
  }
}
