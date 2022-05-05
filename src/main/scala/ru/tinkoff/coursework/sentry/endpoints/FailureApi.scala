package ru.tinkoff.coursework.sentry.endpoints

import ru.tinkoff.coursework.sentry.services.FailureService
import cats.effect._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import ru.tinkoff.coursework.sentry.entities.FailureEntity

class FailureApi(failuresService: FailureService) {

  implicit def failureEventEncoder: EntityEncoder[IO, FailureEntity] = jsonEncoderOf

  implicit def failureEventDecoder: EntityDecoder[IO, FailureEntity] = jsonOf

  val failureRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "failures" / IntVar(id) =>
      failuresService.find(id).flatMap(Ok(_))
    case req@POST -> Root / "failures" / UUIDVar(user) =>
      for {
        failureEvent <- req.as[FailureEntity]
        recordResult <- failuresService.recordFailure(failureEvent)
        resp <- Ok(recordResult)
      } yield resp
  }
}
