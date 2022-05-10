package ru.tinkoff.coursework.sentry.database

import cats.effect._
import cats.implicits.toTraverseOps
import doobie._
import doobie.implicits._
import doobie.h2._
import doobie.h2.implicits._
import doobie.implicits.javasql._
import doobie.util.transactor.Transactor.Aux
import ru.tinkoff.coursework.sentry.entities.{FailureEntity, JobEntity, ServiceEntity, UserEntity}
import ru.tinkoff.coursework.sentry.database.SentryDatabase
import java.sql.Timestamp
import java.util.UUID

class SentryDatabaseImpl extends SentryDatabase {

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
        tag VARCHAR NOT NULL,
        FOREIGN KEY (userId)  REFERENCES userTable (userId)
        )
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
        tag VARCHAR NOT NULL,
        FOREIGN KEY (serviceId)  REFERENCES serviceTable (serviceId)
        )
       """.update.run.transact(xa)

  val serviceUserSubscribeScheme: IO[Int] =
    sql"""
      CREATE TABLE serviceUserTable (
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

  val jobUserScheme: IO[Int] =
    sql"""
      CREATE TABLE jobUserTable (
        id LONG AUTO_INCREMENT PRIMARY KEY,
        userId UUID NOT NULL,
        jobId LONG NOT NULL,
        FOREIGN KEY (jobId) REFERENCES jobTable (jobId)
        )
       """.update.run.transact(xa)

  def createServiceTag(serviceId: Long, tag: String): IO[Boolean] = {
    sql"""
      INSERT INTO serviceTagTable (serviceId,tag)
      VALUES ($serviceId,$tag)"""
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }

  def createUserTag(userId: UUID, tag: String): IO[Boolean] = {
    sql"""
      INSERT INTO userTagTable (userId,tag)
      VALUES ($userId,$tag)"""
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }

  def createService(service: ServiceEntity): IO[Boolean] = {
    sql"""
      INSERT INTO serviceTable (serviceId,URL)
      VALUES (${service.serviceId},${service.URL})"""
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }


  def createUser(user: UserEntity): IO[Boolean] = {
    sql"""
      INSERT INTO userTable (userId,username,mail,cellphone)
      VALUES (${user.userId},${user.username},${user.mail},${user.cellphone})"""
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }

  def createFailure(failure: FailureEntity): IO[Boolean] = {
    sql"""
      INSERT INTO failureTable (failureId,URL,description,failureTime)
      VALUES (${failure.failureId},${failure.URL},${failure.description},${failure.timestamp})"""
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }

  def createJob(userId: UUID, jobEntity: JobEntity): IO[Boolean] = {
    for {
      jobTableRecord <- createJobTableRecord(jobEntity)
      jobUserTableRecord <- createJobUserTableRecord(userId,jobEntity)
    } yield jobTableRecord && jobUserTableRecord
  }

  private def createJobTableRecord(jobEntity: JobEntity): IO[Boolean] = {
    sql"""
      INSERT INTO jobTable (jobId, serviceId, description, startTime, endTime)
      VALUES (${jobEntity.jobId}, ${jobEntity.serviceId}, ${jobEntity.description}, ${jobEntity.startTime}, ${jobEntity.endTime})
      """
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }

  private def createJobUserTableRecord(userId:UUID, jobEntity: JobEntity): IO[Boolean] = {
    sql"""
      INSERT INTO jobUserTable (userId,jobId)
      VALUES ($userId, ${jobEntity.jobId})"""
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }

  def assignUserToService(userId: UUID, serviceId: Long): IO[Boolean] = {
    sql"""
      INSERT INTO serviceUserTable (userId,serviceId)
      VALUES ($userId,$serviceId)"""
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }


  def findJobById(id: Long): IO[Option[JobEntity]] = {
    sql"""SELECT jobTable.jobId, jobTable.serviceId, jobTable.description, jobTable.startTime, jobTable.endTime
      FROM userTable WHERE jobTable.jobId = $id"""
      .query[JobEntity]
      .option
      .transact(xa)
  }

  def findServiceById(id: Long): IO[Option[ServiceEntity]] = {
    sql"""SELECT serviceTable.serviceId, serviceTable.URL
      FROM failureTable WHERE serviceTable.serviceId = $id"""
      .query[ServiceEntity]
      .option
      .transact(xa)
  }

  def findUserById(userId: UUID): IO[Option[UserEntity]] = {
    sql"""SELECT userTable.userId, userTable.username, userTable.mail, userTable.cellphone
      FROM userTable WHERE userTable.userId = $userId"""
      .query[UserEntity]
      .option
      .transact(xa)
  }

  def findServicesByTag(tag: String): IO[List[ServiceEntity]] = {
    sql"""SELECT serviceTable.serviceId, serviceTable.URL
      FROM serviceTable JOIN serviceTagTable ON serviceTable.serviceId = serviceTagTable.serviceId
      WHERE serviceTagTable.tag = $tag"""
      .query[ServiceEntity]
      .to[List]
      .transact(xa)
  }

  def findUsersByTag(tag: String): IO[List[UserEntity]] = {
    sql"""SELECT userTable.userId, userTable.username, userTable.mail, userTable.cellphone
      FROM userTable JOIN userTagTable ON userTable.userId = userTagTable.userId
      WHERE userTagTable.tag = $tag"""
      .query[UserEntity]
      .to[List]
      .transact(xa)
  }

  def findFailureById(id: Long): IO[Option[FailureEntity]] = {
    sql"""SELECT failureTable.failureId, failureTable.URL, failureTable.description, failureTable.failureTime
      FROM failureTable WHERE failureTable.failureId = $id"""
      .query[FailureEntity]
      .option
      .transact(xa)
  }


  def getUsersByServiceId(serviceId: Long): IO[Set[UserEntity]] = {
    sql"""SELECT userTable.userId, userTable.username, userTable.mail, userTable.cellphone
       FROM serviceUserTable JOIN userTable ON serviceUserTable.userId = userTable.userId
       WHERE serviceUserTable.serviceId = $serviceId """
      .query[UserEntity]
      .to[Set]
      .transact(xa)
  }


  def getServiceId(URL: String): IO[Option[Long]] = {
    sql"""SELECT serviceTable.serviceId
      FROM serviceTable WHERE serviceTable.URL = $URL"""
      .query[Long]
      .option
      .transact(xa)
  }

  def getTagsByServiceId(serviceId: Long): IO[Set[String]] = {
    sql"""
         SELECT serviceTagTable.tag
         FROM serviceTagTable WHERE serviceTagTable.serviceId = $serviceId """
      .query[String]
      .to[Set]
      .transact(xa)
  }

  def getUsersDutyInJobs(currentTime: Timestamp): IO[Set[UserEntity]] = {
    val getSetOfUserIdsDutyInJobs: IO[Set[UUID]] =sql"""SELECT jobUserTable.userId
      FROM jobTable JOIN jobUserTable ON jobTable.jobId = jobUserTable.jobId
      WHERE jobTable.startTime < $currentTime
      AND jobTable.endTime > $currentTime
     """
      .query[UUID]
      .to[Set]
      .transact(xa)
    for {
      id <- getSetOfUserIdsDutyInJobs
      res <- getUsersByIds(id)
    } yield res
  }

  private def getUsersByIds(userIdList: Set[UUID]): IO[Set[UserEntity]] = {
    userIdList.toList
      .map(userId =>
        findUserById(userId).map(_.get)
      )
      .sequence
      .map(list => list.toSet)
  }

  def getUsersByTags(tagList: Set[String]): IO[Set[UserEntity]] = {
    tagList.toList
      .map(tag =>
        findUsersByTag(tag)
      )
      .sequence
      .map(list => list.flatten.toSet)
  }
}