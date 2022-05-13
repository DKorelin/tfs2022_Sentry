package ru.tinkoff.coursework.sentry.database

import cats._
import cats.data._
import cats.effect._
import doobie._
import cats.implicits._
import doobie.implicits._
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.implicits.javasql._
import doobie.implicits.legacy.instant._
import doobie.implicits.legacy.localdate._
import doobie.util.transactor.Transactor.Aux
import ru.tinkoff.coursework.sentry.entities.JobEntity
import java.sql.Timestamp

class JobDAOImpl(xa: Aux[IO, Unit]) extends JobDAO {

  override def createJob(userId: Long, jobEntity: JobEntity): IO[Boolean] = {
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

  private def createJobUserTableRecord(userId:Long, jobEntity: JobEntity): IO[Boolean] = {
    sql"""
      INSERT INTO jobUserTable (userId,jobId)
      VALUES ($userId, ${jobEntity.jobId})"""
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }

  override def findJobById(id: Long): IO[Option[JobEntity]] = {
    sql"""SELECT jobTable.jobId, jobTable.serviceId, jobTable.description, jobTable.startTime, jobTable.endTime
      FROM userTable WHERE jobTable.jobId = $id"""
      .query[JobEntity]
      .option
      .transact(xa)
  }
}
