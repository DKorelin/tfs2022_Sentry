import cats.effect.{ExitCode, IO, IOApp}
import ru.tinkoff.coursework.sentry.Sentry
import ru.tinkoff.coursework.sentry.Sentry.xa
import ru.tinkoff.coursework.sentry.alertManager.{AlertManager, AlertManagerImpl}
import ru.tinkoff.coursework.sentry.database.{FailureDAOImpl, JobDAOImpl, Schemes, ServiceDAOImpl, TagDAOImpl, TelegramDAOImpl, UserDAOImpl}
import ru.tinkoff.coursework.sentry.entities.{FailureEntity, JobEntity, ServiceEntity, TagEntity, UserEntity}
import ru.tinkoff.coursework.sentry.services.{FailureServiceImpl, JobServiceImpl, ServiceServiceImpl, TagServiceImpl, UserServiceImpl}

import java.sql.Timestamp
import java.time.LocalDateTime

object ServicesDemo extends IOApp {
  val database = new Schemes(xa)
  val failureDAO: FailureDAOImpl = new FailureDAOImpl(xa)
  val jobDAO: JobDAOImpl = new JobDAOImpl(xa)
  val serviceDAO: ServiceDAOImpl = new ServiceDAOImpl(xa)
  val tagDAO: TagDAOImpl = new TagDAOImpl(xa)
  val telegramDAO: TelegramDAOImpl = new TelegramDAOImpl(xa)
  val userDAO: UserDAOImpl = new UserDAOImpl(xa)
  val schemes: IO[Unit] = for {
    _ <- database.dropTables
    _ <- database.userScheme
    _ <- database.userTagScheme
    _ <- database.serviceScheme
    _ <- database.serviceTagScheme
    _ <- database.failureScheme
    _ <- database.jobScheme
    _ <- database.jobUserScheme
    _ <- database.serviceUserSubscribeScheme
    _ <- database.userWithTelegramChatScheme
  } yield ()

  val alertManager: AlertManager = new AlertManagerImpl(serviceDAO, tagDAO, userDAO, telegramDAO)
  val failureService = new FailureServiceImpl(failureDAO, alertManager)
  val userService = new UserServiceImpl(userDAO)
  val serviceService = new ServiceServiceImpl(Sentry.serviceDAO)
  val tagService = new TagServiceImpl(tagDAO)
  val jobService = new JobServiceImpl(jobDAO)

  override def run(args: List[String]): IO[ExitCode] = {
    val demoId1 = 1
    val demoId2 = 2
    val demoId3 = 3
    val demoId4 = 4
    val demoUserByService = UserEntity(demoId1, "bob", "dummy@mail.com", "8-800-555-35-35")
    val demoUserByTag = UserEntity(demoId2, "Mr. Watcher", "watcher@mail.com", "8-800-100-70-10")
    val demoUserByJob = UserEntity(demoId3, "Mr. Jobman", "jobman@mail.com", "8-800-555-777-8")
    val demoUserFutureJobs = UserEntity(demoId4, "Mr. Future", "future@mail.com", "8-800-333-32-36")
    val demoURL = "www.dummy.com"
    val currentTime = Timestamp.valueOf(LocalDateTime.now())
    val futureTime = Timestamp.valueOf(LocalDateTime.now().plusSeconds(5))
    val farFutureTime = Timestamp.valueOf(LocalDateTime.now().plusSeconds(10))
    val pastTime = Timestamp.valueOf(LocalDateTime.now().minusSeconds(5))
    val demoService = ServiceEntity(1, demoURL)
    val demoFailure = FailureEntity(demoURL, " Epic failure. Hacker is n00b1e", currentTime)
    val demoTag = TagEntity("DemoTag")
    val demoJob = JobEntity(1,1,"demo job",pastTime,futureTime)
    val demoJobPlanning = JobEntity(2,1,"future demo job",futureTime,farFutureTime)
    for {
      _ <- schemes
      _ <- userService.createUser(demoUserByService)
      _ <- userService.createUser(demoUserByTag)
      _ <- userService.createUser(demoUserByJob)
      _ <- serviceService.createService(demoService)
      _ <- serviceService.assignUserToService(demoUserByService.userId, demoService)
      _ <- tagService.createUserTag(demoUserByTag.userId,demoTag)
      _ <- tagService.createUserTag(demoUserByService.userId,demoTag)
      _ <- tagService.createServiceTag(demoService.serviceId,demoTag)
      _ <- jobService.createJob(demoUserByJob.userId,demoJob)
      _ <- jobService.createJob(demoUserFutureJobs.userId,demoJobPlanning)
      _ <- failureService.recordFailure(demoFailure)
      //expecting user print caused by alertManager
    } yield ExitCode.Success
  }
}
