package ru.tinkoff.coursework.sentry.services

import cats.effect.unsafe.implicits.global
import org.scalatest.funsuite.AsyncFunSuite
import ru.tinkoff.coursework.sentry.entities.{ServiceEntity, TagEntity}


class ServiceServiceImplTest extends AsyncFunSuite {
  val testUserId = 1
  val testServiceId = 2
  val testURL = "www.dummy.com"
  val testService: ServiceEntity = ServiceEntity(testServiceId,testURL)
  val testTag: TagEntity = TagEntity("testTag")

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

  test("test findServicesByTag") {
    val tagService = new TagServiceImpl(DatabaseMock)
    val serviceService = new ServiceServiceImpl(DatabaseMock)
    DatabaseMock.listOfServices.addOne(testService)
    val testBody = for {
      _ <- tagService.createServiceTag(testServiceId, testTag)
      receivedServices <- serviceService.findServicesByTag(testTag.tag)
    } yield assert(receivedServices.contains(testService))
    testBody.unsafeRunSync()
  }
}
