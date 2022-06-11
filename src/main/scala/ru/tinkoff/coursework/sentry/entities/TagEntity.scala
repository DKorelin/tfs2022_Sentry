package ru.tinkoff.coursework.sentry.entities

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class TagEntity(tag: String)

object TagEntity {
  implicit val jsonUserDecoder: Decoder[TagEntity] = deriveDecoder

  implicit val jsonUserEncoder: Encoder[TagEntity] = deriveEncoder
}
