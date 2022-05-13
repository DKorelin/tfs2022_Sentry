package ru.tinkoff.coursework.sentry.services

import cats.effect.unsafe.implicits.global
import org.scalatest.funsuite.AsyncFunSuite
import ru.tinkoff.coursework.sentry.entities.{TagEntity, UserEntity}


class UserServiceImplTest extends AsyncFunSuite {
  val testUserId = 1
  val testUser: UserEntity = UserEntity(testUserId,"testUsername","testEmail","testCellphone")
  val testTag: TagEntity = TagEntity("testTag")

  test("test createUser") {
    val userService = new UserServiceImpl(DatabaseMock)
    val testBody = for {
      _ <- userService.createUser(testUser)
    } yield assert(DatabaseMock.listOfUsers.contains(testUser))
    testBody.unsafeRunSync()
  }

  test("test findUser") {
    val userService = new UserServiceImpl(DatabaseMock)
    val testBody = for {
      _ <- userService.createUser(testUser)
      receivedUser <- userService.findUser(testUserId)
    } yield assert(receivedUser.contains(testUser))
    testBody.unsafeRunSync()
  }

  test("test findUsersByTag") {
    val tagService = new TagServiceImpl(DatabaseMock)
    val userService = new UserServiceImpl(DatabaseMock)
    DatabaseMock.listOfUsers.addOne(testUser)
    val testBody = for {
      _ <- tagService.createUserTag(testUserId,testTag)
      receivedUsers <- userService.findUsersByTag(testTag.tag)
    } yield assert(receivedUsers.contains(testUser))
    testBody.unsafeRunSync()
  }
}
