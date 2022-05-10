import cats.effect.{ExitCode, IO, IOApp}
import ru.tinkoff.coursework.sentry.database.SentryDatabaseImpl
import ru.tinkoff.coursework.sentry.entities.{ServiceEntity, UserEntity}
import ru.tinkoff.coursework.sentry.services.ServiceServiceImpl

import java.util.UUID

object ServiceServiceTest extends IOApp {

  val database: SentryDatabaseImpl = new SentryDatabaseImpl
  val demoUUID: UUID = UUID.randomUUID()
  val demoUser: UserEntity = UserEntity(demoUUID, "bob", "dummy@mail.com", "8-800-555-35-35")
  val demoURL = "www.dummy.com"
  val demoService: ServiceEntity = ServiceEntity(1, demoURL)
  val serviceService: ServiceServiceImpl = new ServiceServiceImpl(database)

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- database.userScheme
      _ <- database.serviceScheme
      _ <- database.serviceUserSubscribeScheme
      createUserResult <- database.createUser(demoUser)
      _ <- IO(println(s"createUserResult $createUserResult"))
      createServiceResult <- serviceService.createService(demoService)
      _ <- IO(println(s"createServiceResult $createServiceResult"))
      tagUserToServiceResult <- serviceService.assignUserToService(demoUUID, demoService)
      _ <- IO(println(s"tagUserToServiceResult $tagUserToServiceResult"))
    } yield ExitCode.Success
  }
}
