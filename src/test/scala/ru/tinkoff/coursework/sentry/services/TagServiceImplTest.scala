package ru.tinkoff.coursework.sentry.services

import cats.effect.unsafe.implicits.global
import org.scalatest.funsuite.AsyncFunSuite
import ru.tinkoff.coursework.sentry.entities.{ServiceEntity, TagEntity, UserEntity}

class TagServiceImplTest extends AsyncFunSuite {
  val testUserId = 1
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

  test("test createServiceTag") {
    val tagService = new TagServiceImpl(DatabaseMock)
    val testBody = for {
      _ <- tagService.createServiceTag(testServiceId, testTag)
    } yield assert(DatabaseMock.serviceTagMap.contains(testServiceId))
    testBody.unsafeRunSync()
  }
}
