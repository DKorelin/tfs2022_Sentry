package ru.tinkoff.coursework.sentry.database

import cats.effect._
import cats.implicits.toTraverseOps
import doobie._
import doobie.implicits._
import doobie.h2._
import doobie.h2.implicits._
import doobie.implicits.javasql._
import doobie.util.transactor.Transactor.Aux
import ru.tinkoff.coursework.sentry.entities.{FailureEntity, ServiceEntity, UserEntity}

import java.util.UUID

class SentryDatabase {
  def tagUserToService(userId: UUID, serviceId: Long): IO[Boolean] = {
    sql"""
      INSERT INTO serviceUserSubscribeTable (userId,serviceId)
      VALUES ($userId,$serviceId)"""
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }


  def createService(service: ServiceEntity): IO[Int] = {
    sql"""
      INSERT INTO serviceTable (serviceId,URL)
      VALUES (${service.serviceId},${service.URL})"""
      .update
      .run
      .transact(xa)
  }


  def createUser(user: UserEntity): IO[Int] = {
    sql"""
      INSERT INTO userTable (userId,username,mail,cellphone)
      VALUES (${user.userId},${user.username},${user.mail},${user.cellphone})"""
      .update
      .run
      .transact(xa)
  }

  def findUserById(userId: UUID): IO[UserEntity] = {
    sql"""SELECT userTable.userId, userTable.username, userTable.mail, userTable.cellphone
      FROM userTable WHERE userTable.userId = $userId"""
      .query[UserEntity]
      .unique
      .transact(xa)
  }

  def getUsersById(alertIdList: Set[UUID]): IO[Set[UserEntity]] = {
    alertIdList.toList
      .map(userId =>
        findUserById(userId)
      )
      .sequence
      .map(list => list.toSet)
  }

  def getJobsIdByTagsId(tagList: Set[Long]): IO[Set[UUID]] = IO(Set.empty) //???

  def getUsersIdByServiceId(serviceId: Long): IO[Set[UUID]] = {
    sql"""
         SELECT serviceUserSubscribeTable.userId
         FROM serviceUserSubscribeTable WHERE serviceUserSubscribeTable.serviceId = $serviceId """
      .query[UUID]
      .to[Set]
      .transact(xa)
  }

  def getUsersIdByJobId(jobList: Any): IO[Set[UUID]] = IO(Set.empty) //???

  def getUsersIdByTagsId(tagList: Set[Long]): IO[Set[UUID]] = IO(Set.empty) //???

  val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    "org.h2.Driver", "jdbc:h2:mem:default;DB_CLOSE_DELAY=-1", "sa", ""
  )

  val userScheme: IO[Int] =
    sql"""
      CREATE TABLE userTable (
        userId UUID NOT NULL PRIMARY KEY,
        username VARCHAR NOT NULL,
        mail VARCHAR,
        cellphone VARCHAR
        )
       """.update.run.transact(xa)

  val userTagScheme: IO[Int] =
    sql"""
      CREATE TABLE userTagTable (
        id long AUTO_INCREMENT PRIMARY KEY,
        userId UUID NOT NULL,
        tag VARCHAR NOT NULL)
       """.update.run.transact(xa)

  val serviceScheme: IO[Int] =
    sql"""
      CREATE TABLE serviceTable (
        serviceId LONG NOT NULL PRIMARY KEY,
        URL VARCHAR NOT NULL)
   """.update.run.transact(xa)

  val serviceTagScheme: IO[Int] =
    sql"""
      CREATE TABLE serviceTagTable (
        id LONG AUTO_INCREMENT PRIMARY KEY,
        serviceId LONG NOT NULL,
        tag VARCHAR NOT NULL)
       """.update.run.transact(xa)

  val serviceUserSubscribeScheme: IO[Int] =
    sql"""
      CREATE TABLE serviceUserSubscribeTable (
        id LONG AUTO_INCREMENT PRIMARY KEY,
        userId UUID NOT NULL,
        serviceId LONG NOT NULL)
       """.update.run.transact(xa)

  val failureScheme: IO[Int] =
    sql"""
      CREATE TABLE failureTable (
        failureId LONG NOT NULL PRIMARY KEY,
        URL VARCHAR NOT NULL,
        description VARCHAR,
        failureTime TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
      )
   """.update.run.transact(xa)

  val jobScheme: IO[Int] =
    sql"""
      CREATE TABLE jobTable (
        jobId LONG NOT NULL PRIMARY KEY,
        serviceId LONG NOT NULL,
        description VARCHAR,
        startTime TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
        endTime TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
      )
   """.update.run.transact(xa)

  val jobUserSubscribeScheme: IO[Int] =
    sql"""
      CREATE TABLE jobUserSubscribeTable (
        id LONG AUTO_INCREMENT PRIMARY KEY,
        userId UUID NOT NULL,
        jobId LONG NOT NULL)
       """.update.run.transact(xa)

  def writeFailure(failure: FailureEntity): IO[Int] = {
    sql"""
      INSERT INTO failureTable (failureId,URL,description,failureTime)
      VALUES (${failure.failureId},${failure.URL},${failure.description},${failure.timestamp})"""
      .update
      .run
      .transact(xa)
  }

  def readFailure(id: Long): IO[Option[FailureEntity]] = {
    sql"""SELECT {failureTable.failureId,failureTable.URL,failureTable.description,failureTable.failureTime}
      FROM failureTable WHERE failureId = $id"""
      .query[FailureEntity]
      .option
      .transact(xa)
  }

  def getServiceId(URL: String): IO[Long] = {
    sql"""SELECT serviceTable.serviceId
      FROM serviceTable WHERE serviceTable.URL = $URL"""
      .query[Long]
      .unique
      .transact(xa)
  }

  def getTagsIdByServiceId(serviceId: Long): IO[Set[Long]] = {
    sql"""
         SELECT serviceTagTable.tag
         FROM serviceTagTable WHERE serviceTagTable.serviceId = $serviceId """
      .query[Long]
      .to[Set]
      .transact(xa)
  }
}
