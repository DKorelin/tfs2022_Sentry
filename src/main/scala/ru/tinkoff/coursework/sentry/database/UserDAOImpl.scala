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
import doobie.util.transactor.Transactor.Aux
import ru.tinkoff.coursework.sentry.entities.UserEntity
import java.sql.Timestamp

class UserDAOImpl(xa: Aux[IO, Unit]) extends UserDAO {
  override def findUserById(userId: Long): IO[Option[UserEntity]] = {
    sql"""SELECT userTable.userId, userTable.username, userTable.mail, userTable.cellphone
      FROM userTable WHERE userTable.userId = $userId"""
      .query[UserEntity]
      .option
      .transact(xa)
  }

  override   def findUsersByTag(tag: String): IO[List[UserEntity]] = {
    sql"""SELECT userTable.userId, userTable.username, userTable.mail, userTable.cellphone
      FROM userTable JOIN userTagTable ON userTable.userId = userTagTable.userId
      WHERE userTagTable.tag = $tag"""
      .query[UserEntity]
      .to[List]
      .transact(xa)
  }

  override def createUser(user: UserEntity): IO[Boolean] = {
    sql"""
      INSERT INTO userTable (userId,username,mail,cellphone)
      VALUES (${user.userId},${user.username},${user.mail},${user.cellphone})"""
      .update
      .run
      .map(_ > 0)
      .transact(xa)
  }

  override def getUsersDutyInJobs(currentTime: Timestamp): IO[Set[UserEntity]] = {
    val getSetOfUserIdsDutyInJobs: IO[Set[Long]] =sql"""SELECT jobUserTable.userId
      FROM jobTable JOIN jobUserTable ON jobTable.jobId = jobUserTable.jobId
      WHERE jobTable.startTime < $currentTime
      AND jobTable.endTime > $currentTime
     """
      .query[Long]
      .to[Set]
      .transact(xa)
    for {
      id <- getSetOfUserIdsDutyInJobs
      res <- getUsersByIds(id)
    } yield res
  }

  private def getUsersByIds(userIdList: Set[Long]): IO[Set[UserEntity]] = {
    userIdList.toList
      .map(userId =>
        findUserById(userId).map(_.get)
      )
      .sequence
      .map(list => list.toSet)
  }

  override def getUsersByServiceId(serviceId: Long): IO[Set[UserEntity]] = {
    sql"""SELECT userTable.userId, userTable.username, userTable.mail, userTable.cellphone
       FROM serviceUserTable JOIN userTable ON serviceUserTable.userId = userTable.userId
       WHERE serviceUserTable.serviceId = $serviceId """
      .query[UserEntity]
      .to[Set]
      .transact(xa)
  }

  override def getUsersByTags(tagList: Set[String]): IO[Set[UserEntity]] = {
    tagList.toList
      .map(tag =>
        findUsersByTag(tag)
      )
      .sequence
      .map(list => list.flatten.toSet)
  }
}
