package ru.tinkoff.coursework.sentry.endpoints

import cats.effect._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl.io.{:?, _}
import ru.tinkoff.coursework.sentry.entities.TagEntity
import ru.tinkoff.coursework.sentry.services.TagServiceImpl

class TagApi(tagService: TagServiceImpl) {
  implicit def tagEncoder: EntityEncoder[IO, TagEntity] = jsonEncoderOf

  implicit def tagDecoder: EntityDecoder[IO, TagEntity] = jsonOf

  object UserQueryParamMatcher extends QueryParamDecoderMatcher[Long]("user")

  object ServiceQueryParamMatcher extends QueryParamDecoderMatcher[Long]("service")

  val tagRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req@POST -> Root / "tags" :? UserQueryParamMatcher(userId) =>
      for {
        tagEntity <- req.as[TagEntity]
        tagUserResult <- tagService.createUserTag(userId, tagEntity)
        resp <- Ok(tagUserResult)
      } yield resp
    case req@POST -> Root / "tags" :? ServiceQueryParamMatcher(serviceId) =>
      for {
        tagEntity <- req.as[TagEntity]
        tagUserResult <- tagService.createServiceTag(serviceId, tagEntity)
        resp <- Ok(tagUserResult)
      } yield resp
  }
}
