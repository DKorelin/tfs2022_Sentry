package ru.tinkoff.coursework.sentry.services

import cats.effect.unsafe.implicits.global
import org.scalatest.funsuite.AsyncFunSuite
import ru.tinkoff.coursework.sentry.entities.JobEntity
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID

class JobServiceImplTest extends AsyncFunSuite {
  val testUserId: UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
  val testServiceId = 2
  val testJobId = 4
  val testTime: Timestamp = Timestamp.valueOf(LocalDateTime.parse("2007-12-03T10:15:30"))
  val testDescription = "Job #Test"
  val expectedJob: JobEntity = JobEntity(testJobId,testServiceId,testDescription,testTime,testTime)

  test("test createJob") {
    val jobService = new JobServiceImpl(DatabaseMock)
    val testBody = for {
      _ <- jobService.createJob(testUserId,expectedJob)
    } yield assert(DatabaseMock.listOfJobs.contains(expectedJob))
    testBody.unsafeRunSync()
  }

  test("test findJob") {
    val jobService = new JobServiceImpl(DatabaseMock)
    val testBody = for {
      _ <- jobService.createJob(testUserId,expectedJob)
      receivedJob <- jobService.findJob(testJobId)
    } yield assert(receivedJob.contains(expectedJob))
    testBody.unsafeRunSync()
  }
}
