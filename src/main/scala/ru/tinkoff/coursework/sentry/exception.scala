package ru.tinkoff.coursework.sentry

sealed abstract class SentryException(message: String)
  extends Exception(message)

final case class FailureNotFoundException(failureId: Long)
  extends SentryException(s"FailureEvent with id=$failureId not found")

final case class ServiceIdNotFoundException()
  extends SentryException(s"Service not found")

final case class UserIdNotFoundException()
  extends SentryException(s"User not found")

final case class JobNotFoundException(jobId: Long)
  extends SentryException(s"Job with id=$jobId not found")
