package ru.tinkoff.coursework.sentry.endpoints

import cats.effect._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl.io._
import ru.tinkoff.coursework.sentry.entities.ServiceEntity
import ru.tinkoff.coursework.sentry.services.ServiceService

class ServiceApi(serviceService: ServiceService) {
  implicit def serviceEncoder: EntityEncoder[IO, ServiceEntity] = jsonEncoderOf

  implicit def serviceDecoder: EntityDecoder[IO, ServiceEntity] = jsonOf

  val serviceRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "services" / LongVar(id) =>
      serviceService.findService(id).flatMap(Ok(_))
    case req@POST -> Root / "services" =>
      for {
        service <- req.as[ServiceEntity]
        recordResult <- serviceService.createService(service)
        resp <- Ok(recordResult)
      } yield resp
    case req@POST -> Root / "services" / "subscribe" / UUIDVar(userId) =>
      for {
        service <- req.as[ServiceEntity]
        recordResult <- serviceService.tagUserToService(userId, service)
        resp <- Ok(recordResult)
      } yield resp
  }

}
