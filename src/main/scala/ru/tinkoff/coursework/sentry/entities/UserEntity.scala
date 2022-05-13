package ru.tinkoff.coursework.sentry.entities

import io.circe.Decoder.Result
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.{Decoder, Encoder, HCursor, Json}

case class UserEntity(userId: Long, username: String, mail: String, cellphone: String)

object UserEntity {
  implicit val jsonUserDecoder: Decoder[UserEntity] = new Decoder[UserEntity] {
    final def apply(c: HCursor): Result[UserEntity] = {
      for {
        userId <- c.downField("userId").as[Long]
        username <- c.downField("username").as[String]
        mail <- c.downField("mail").as[String]
        cellphone <- c.downField("cellphone").as[String]
      } yield UserEntity(userId, username, mail, cellphone)
    }
  }
  implicit val jsonUserEncoder: Encoder[UserEntity] = new Encoder[UserEntity] {
    final def apply(a: UserEntity): Json = Json.obj(
      ("userId", Json.fromString(a.userId.toString)),
      ("username", Json.fromString(a.username)),
      ("mail", Json.fromString(a.mail)),
      ("cellphone", Json.fromString(a.cellphone))
    )
  }
}