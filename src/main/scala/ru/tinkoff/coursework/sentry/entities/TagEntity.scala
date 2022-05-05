package ru.tinkoff.coursework.sentry.entities

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

case class TagEntity(tag: String)

object TagEntity {
  implicit val jsonUserDecoder: Decoder[TagEntity] = new Decoder[TagEntity] {
    final def apply(c: HCursor): Result[TagEntity] = {
      for {
        tag <- c.downField("tag").as[String]
      } yield TagEntity(tag)
    }
  }
  implicit val jsonUserEncoder: Encoder[TagEntity] = new Encoder[TagEntity] {
    final def apply(a: TagEntity): Json = Json.obj(
      ("tag", Json.fromString(a.tag))
    )
  }
}
