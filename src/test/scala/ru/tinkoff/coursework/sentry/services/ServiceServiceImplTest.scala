package ru.tinkoff.coursework.sentry.services

import cats.effect.unsafe.implicits.global
import org.scalatest.funsuite.AsyncFunSuite
import ru.tinkoff.coursework.sentry.entities.ServiceEntity
import java.util.UUID

class ServiceServiceImplTest extends AsyncFunSuite {
  val testUserId: UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
  val testServiceId = 2
  val testURL = "www.dummy.com"
  val testService: ServiceEntity = ServiceEntity(testServiceId,testURL)

  test("test createService") {
    val serviceService = new ServiceServiceImpl(DatabaseMock)
    val testBody = for {
      _ <- serviceService.createService(testService)
    } yield assert(DatabaseMock.listOfServices.contains(testService))
    testBody.unsafeRunSync()
  }

  test("test findService") {
    val serviceService = new ServiceServiceImpl(DatabaseMock)
    val testBody = for {
      _ <- serviceService.createService(testService)
      receivedService <- serviceService.findService(testServiceId)
    } yield assert(receivedService.contains(testService))
    testBody.unsafeRunSync()
  }

  test("test assignUserToService") {
    val serviceService = new ServiceServiceImpl(DatabaseMock)
    val testBody = for {
      _ <- serviceService.createService(testService)
      _ <- serviceService.assignUserToService(testUserId,testService)
    } yield assert(DatabaseMock.userServicesMap.contains(testUserId))
    testBody.unsafeRunSync()
  }
}
