package ru.tinkoff.coursework.sentry.services

import cats.effect.unsafe.implicits.global
import org.scalatest.funsuite.AsyncFunSuite
import ru.tinkoff.coursework.sentry.entities.UserEntity
import java.util.UUID

class UserServiceImplTest extends AsyncFunSuite {
  val testUserId: UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
  val testUser: UserEntity = UserEntity(testUserId,"testUsername","testEmail","testCellphone")

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
}
