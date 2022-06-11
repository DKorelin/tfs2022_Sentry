package ru.tinkoff.coursework.sentry.services

import cats.effect.IO
import ru.tinkoff.coursework.sentry.database.{FailureDAO, JobDAO, ServiceDAO, TagDAO, TelegramDAO, UserDAO}
import ru.tinkoff.coursework.sentry.entities.{FailureEntity, JobEntity, ServiceEntity, UserEntity}

import java.sql.Timestamp
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

object DatabaseMock extends FailureDAO with JobDAO with ServiceDAO with TagDAO with UserDAO with TelegramDAO{
  var listOfJobs: ListBuffer[JobEntity] = scala.collection.mutable.ListBuffer[JobEntity]()
  var listOfFailures: ArrayBuffer[FailureEntity] = scala.collection.mutable.ArrayBuffer[FailureEntity]()
  var listOfServices: ListBuffer[ServiceEntity] = scala.collection.mutable.ListBuffer[ServiceEntity]()
  var listOfUsers: ListBuffer[UserEntity] = scala.collection.mutable.ListBuffer[UserEntity]()
  var userServicesMap = scala.collection.mutable.HashMap.empty[Long,Long]
  var userTagMap = scala.collection.mutable.HashMap.empty[Long,String]
  var serviceTagMap = scala.collection.mutable.HashMap.empty[Long,String]

  override def createJob(userId: Long, jobEntity: JobEntity): IO[Boolean] = IO {
    listOfJobs = jobEntity +: listOfJobs
    true
  }

  override def createService(service: ServiceEntity): IO[Boolean] = IO {
    listOfServices = service +: listOfServices
    true
  }

  override def createUser(user: UserEntity): IO[Boolean] = IO {
    listOfUsers = user +: listOfUsers
    true
  }

  override def createFailure(failure: FailureEntity): IO[Long] = IO {
    listOfFailures = failure +: listOfFailures
    listOfFailures.size - 1
  }

  override def createServiceTag(serviceId: Long, tag: String): IO[Boolean] = IO{
    serviceTagMap.addOne(serviceId,tag)
    true
  }

  override def createUserTag(userId: Long, tag: String): IO[Boolean] = IO{
    userTagMap.addOne(userId,tag)
    true
  }

  override def assignUserToService(userId: Long, serviceId: Long): IO[Boolean] = IO{
    userServicesMap.addOne(userId,serviceId)
    true
  }

  override def findServiceById(id: Long): IO[Option[ServiceEntity]] = IO{
    listOfServices.find(serviceEntity => serviceEntity.serviceId == id)
  }

  override def findFailureById(id: Long): IO[Option[FailureEntity]] = IO{
    Some(listOfFailures.apply(id.toInt))
  }

  override def findJobById(id: Long): IO[Option[JobEntity]] = IO{
    listOfJobs.find(jobEntity => jobEntity.jobId == id)
  }

  override def findUserById(userId: Long): IO[Option[UserEntity]] = IO{
    listOfUsers.find(userEntity => userEntity.userId == userId)
  }

  override def findServicesByTag(tag: String): IO[List[ServiceEntity]] = IO{
    serviceTagMap.find(t => t._2 == tag) match {
      case Some(value) => List(listOfServices.find(service => service.serviceId == value._1).get)
      case None => List.empty
    }
  }

  override def getServiceId(URL: String): IO[Option[Long]] = IO{
    val service = listOfServices.find(serviceEntity => serviceEntity.URL.equals(URL))
    service match {
      case Some(value) => Some(value.serviceId)
      case None => None
    }
  }

  override def getTagsByServiceId(serviceId: Long): IO[Set[String]] = IO{Set.empty}

  override def getUsersByServiceId(serviceId: Long): IO[Set[UserEntity]] = IO{Set.empty}

  override def getUsersDutyInJobs(currentTime: Timestamp): IO[Set[UserEntity]] = IO{Set.empty}

  override def getUsersByTags(tagList: Set[String]): IO[Set[UserEntity]] = IO{Set.empty}

  override def findUsersByTag(tag: String): IO[List[UserEntity]] = IO{
    userTagMap.find(t => t._2 == tag) match {
      case Some(value) => List(listOfUsers.find(user => user.userId == value._1).get)
      case None => List.empty
    }
  }

  override def bindUserWithTelegramChat(sentryId: Long, chatId: Long): IO[Boolean] = ???

  override def getChatByUserId(userId: Long): IO[Option[Long]] = ???

  override def getUserIdByChat(chatId: Long): IO[Option[Long]] = ???
}
