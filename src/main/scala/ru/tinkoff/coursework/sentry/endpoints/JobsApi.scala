package ru.tinkoff.coursework.sentry.endpoints

import cats.effect._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl.io._
import ru.tinkoff.coursework.sentry.entities.JobEntity
import ru.tinkoff.coursework.sentry.services.JobServiceImpl

class JobsApi(jobService: JobServiceImpl) {
  implicit def jobEncoder: EntityEncoder[IO, JobEntity] = jsonEncoderOf

  implicit def jobDecoder: EntityDecoder[IO, JobEntity] = jsonOf

  val serviceRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "jobs" / LongVar(id) =>
      jobService.findJob(id).flatMap(Ok(_))
    case req@POST -> Root / "jobs" / LongVar(userId) =>
      for {
        jobEntity <- req.as[JobEntity]
        recordResult <- jobService.createJob(userId, jobEntity)
        resp <- Ok(recordResult)
      } yield resp
  }
}
