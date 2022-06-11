package ru.tinkoff.coursework.sentry.entities

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class UserEntity(userId: Long, username: String, mail: String, cellphone: String)

object UserEntity {
  implicit val jsonUserDecoder: Decoder[UserEntity] = deriveDecoder

  implicit val jsonUserEncoder: Encoder[UserEntity] = deriveEncoder
}