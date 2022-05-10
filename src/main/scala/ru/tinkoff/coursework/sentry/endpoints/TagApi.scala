package ru.tinkoff.coursework.sentry.endpoints

import cats.effect._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl.io._
import ru.tinkoff.coursework.sentry.entities.TagEntity
import ru.tinkoff.coursework.sentry.services.TagServiceImpl

class TagApi(tagService: TagServiceImpl) {
  implicit def tagEncoder: EntityEncoder[IO, TagEntity] = jsonEncoderOf

  implicit def tagDecoder: EntityDecoder[IO, TagEntity] = jsonOf

  val tagRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "tags" / "getUsers" /tag =>
      tagService.findUsersByTag(tag).flatMap(Ok(_))
    case GET -> Root / "tags" / "getServices" /tag =>
      tagService.findServicesByTag(tag).flatMap(Ok(_))
    case req@POST -> Root / "tags" / "user" / UUIDVar(userId) =>
      for {
        tagEntity <- req.as[TagEntity]
        tagUserResult <- tagService.createUserTag(userId, tagEntity)
        resp <- Ok(tagUserResult)
      } yield resp
    case req@POST -> Root / "tags" / "service" / LongVar(serviceId) =>
      for {
        tagEntity <- req.as[TagEntity]
        tagUserResult <- tagService.createServiceTag(serviceId, tagEntity)
        resp <- Ok(tagUserResult)
      } yield resp
  }
}
