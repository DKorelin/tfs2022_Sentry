package ru.tinkoff.coursework.sentry.endpoints

import ru.tinkoff.coursework.sentry.services.UserService
import cats.effect._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import ru.tinkoff.coursework.sentry.entities.UserEntity

class UserApi(userService: UserService) {
  implicit def userEncoder: EntityEncoder[IO, UserEntity] = jsonEncoderOf

  implicit def userDecoder: EntityDecoder[IO, UserEntity] = jsonOf

  val userRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "users" / UUIDVar(id) =>
      userService.findUser(id).flatMap(Ok(_))
    case req@POST -> Root / "users" =>
      for {
        user <- req.as[UserEntity]
        recordResult <- userService.createUser(user)
        resp <- Ok(recordResult)
      } yield resp
  }
}
