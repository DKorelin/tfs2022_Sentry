package ru.tinkoff.coursework.sentry.services
import cats.effect.unsafe.implicits.global
import org.scalatest.funsuite.AsyncFunSuite
import ru.tinkoff.coursework.sentry.entities.{FailureEntity, ServiceEntity}
import java.sql.Timestamp
import java.time.LocalDateTime

class FailureServiceImplTest extends AsyncFunSuite {
  val testFailureId = 1
  val testServiceId = 1
  val testURL = "www.dummy.com"
  val testService: ServiceEntity = ServiceEntity(testServiceId,testURL)
  val testTime: Timestamp = Timestamp.valueOf(LocalDateTime.parse("2007-12-03T10:15:30"))
  val testDescription = " Epic failure. Hacker is n00b1e"
  val expectedFailure: FailureEntity = FailureEntity(testFailureId, testURL, testDescription, testTime)

  test("test recordFailure") {
    val failureService = new FailureServiceImpl(DatabaseMock, AlertManagerMock)
    val testBody = for {
      _ <- DatabaseMock.createService(testService)
      _ <- failureService.recordFailure(expectedFailure)
    } yield assert(DatabaseMock.listOfFailures.contains(expectedFailure))
    testBody.unsafeRunSync()
  }

  test("test findFailure") {
    val failureService = new FailureServiceImpl(DatabaseMock, AlertManagerMock)
    val testBody = for {
      _ <- DatabaseMock.createService(testService)
      _ <- failureService.recordFailure(expectedFailure)
      receivedFailure <- failureService.findFailure(expectedFailure.failureId)
    } yield assert(receivedFailure.contains(expectedFailure))
    testBody.unsafeRunSync()
  }

  test("test recordFailure causes alert on alert manager") {
    val failureService = new FailureServiceImpl(DatabaseMock, AlertManagerMock)
    val testBody = for {
      _ <- DatabaseMock.createService(testService)
      _ <- failureService.recordFailure(expectedFailure)
    } yield assert(AlertManagerMock.messageObjects.contains(AlertManagerMock.mockUser,expectedFailure))
    testBody.unsafeRunSync()
  }

}
