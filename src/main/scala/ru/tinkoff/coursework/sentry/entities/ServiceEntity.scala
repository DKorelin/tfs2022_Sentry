package ru.tinkoff.coursework.sentry.entities

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class ServiceEntity(serviceId: Long, URL: String)

object ServiceEntity {
  implicit val jsonServiceEntityDecoder: Decoder[ServiceEntity] = deriveDecoder

  implicit val jsonServiceEntityEncoder: Encoder[ServiceEntity] = deriveEncoder
}
