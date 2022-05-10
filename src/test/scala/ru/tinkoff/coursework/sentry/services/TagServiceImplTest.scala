package ru.tinkoff.coursework.sentry.services

import cats.effect.unsafe.implicits.global
import org.scalatest.funsuite.AsyncFunSuite
import ru.tinkoff.coursework.sentry.entities.{ServiceEntity, TagEntity, UserEntity}
import java.util.UUID

class TagServiceImplTest extends AsyncFunSuite {
  val testUserId: UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
  val testServiceId = 2
  val testURL = "www.dummy.com"
  val testService: ServiceEntity = ServiceEntity(testServiceId,testURL)
  val testTag: TagEntity = TagEntity("testTag")
  val testUser: UserEntity = UserEntity(testUserId,"testUsername","testEmail","testCellphone")

  test("test createUserTag") {
    val tagService = new TagServiceImpl(DatabaseMock)
    val testBody = for {
      _ <- tagService.createUserTag(testUserId,testTag)
    } yield assert(DatabaseMock.userTagMap.contains(testUserId))
    testBody.unsafeRunSync()
  }

  test("test findUsersByTag") {
    val tagService = new TagServiceImpl(DatabaseMock)
    DatabaseMock.listOfUsers.addOne(testUser)
    val testBody = for {
      _ <- tagService.createUserTag(testUserId,testTag)
      receivedUsers <- tagService.findUsersByTag(testTag.tag)
    } yield assert(receivedUsers.contains(testUser))
    testBody.unsafeRunSync()
  }

  test("test createServiceTag") {
    val tagService = new TagServiceImpl(DatabaseMock)
    val testBody = for {
      _ <- tagService.createServiceTag(testServiceId, testTag)
    } yield assert(DatabaseMock.serviceTagMap.contains(testServiceId))
    testBody.unsafeRunSync()
  }

  test("test findServicesByTag") {
    val tagService = new TagServiceImpl(DatabaseMock)
    DatabaseMock.listOfServices.addOne(testService)
    val testBody = for {
      _ <- tagService.createServiceTag(testServiceId, testTag)
      receivedServices <- tagService.findServicesByTag(testTag.tag)
    } yield assert(receivedServices.contains(testService))
    testBody.unsafeRunSync()
  }
}
