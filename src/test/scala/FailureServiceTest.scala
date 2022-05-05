import cats.effect.{ExitCode, IO, IOApp}
import ru.tinkoff.coursework.sentry.alertManager.AlertManager
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import ru.tinkoff.coursework.sentry.services.FailureService

object FailureServiceTest extends IOApp {
  val database = new SentryDatabase
  val alertManager: AlertManager = new AlertManager

  val failureService = new FailureService(database, alertManager)

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- IO(println("mock"))
    } yield ExitCode.Success
  }
}