import cats.effect.{ExitCode, IO, IOApp}
import ru.tinkoff.coursework.sentry.alertManager.{AlertManager, AlertManagerImpl}
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import ru.tinkoff.coursework.sentry.services.FailureServiceImpl

object FailureServiceTest extends IOApp {
  val database = new SentryDatabase
  val alertManager: AlertManager = new AlertManagerImpl

  val failureService = new FailureServiceImpl(database, alertManager)

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- IO(println("mock"))
    } yield ExitCode.Success
  }
}