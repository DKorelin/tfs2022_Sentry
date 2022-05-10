import cats.effect.{ExitCode, IO, IOApp}
import ru.tinkoff.coursework.sentry.database.SentryDatabaseImpl
import ru.tinkoff.coursework.sentry.entities.FailureEntity

import java.sql.Timestamp
import java.time.LocalDateTime

object SentryDatabaseTest extends IOApp {
  val sentryDB = new SentryDatabaseImpl

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- sentryDB.failureScheme
      f = FailureEntity(0, "url", "desc", Timestamp.valueOf(LocalDateTime.now()))
      writeResult <- sentryDB.createFailure(f)
      _ <- IO(println(writeResult))
      f <- sentryDB.findFailureById(0)
      _ <- IO(println(f))
    } yield ExitCode.Success
  }
}
