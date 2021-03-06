package ru.tinkoff.coursework.sentry.endpoints

import ru.tinkoff.coursework.sentry.services.FailureService
import cats.effect._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import ru.tinkoff.coursework.sentry.entities.FailureEntity

class FailureApi(failuresService: FailureService) {

  implicit val failureEventEncoder: EntityDecoder[IO, FailureEntity] = jsonOf

  implicit val failureEventDecoder: EntityEncoder[IO, FailureEntity] = jsonEncoderOf


  val failureRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "failures" / LongVar(id) =>
      failuresService.findFailure(id).flatMap(Ok(_))
    case req@POST -> Root / "failures" =>
      for {
        failureEvent <- req.as[FailureEntity]
        recordResult <- failuresService.recordFailure(failureEvent)
        resp <- Ok(s"failure registered as $recordResult.")
      } yield resp
  }
}
