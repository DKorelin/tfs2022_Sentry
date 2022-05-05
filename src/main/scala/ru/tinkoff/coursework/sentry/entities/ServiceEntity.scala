package ru.tinkoff.coursework.sentry.entities

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

case class ServiceEntity(serviceId: Long, URL: String)

object ServiceEntity {
  implicit val jsonServiceEntityDecoder: Decoder[ServiceEntity] = new Decoder[ServiceEntity] {
    final def apply(c: HCursor): Result[ServiceEntity] = {
      for {
        serviceId <- c.downField("serviceId").as[Long]
        description <- c.downField("URL").as[String]
      } yield ServiceEntity(serviceId, description)
    }
  }
  implicit val jsonServiceEntityEncoder: Encoder[ServiceEntity] = new Encoder[ServiceEntity] {
    final def apply(a: ServiceEntity): Json = Json.obj(
      ("serviceId", Json.fromLong(a.serviceId)),
      ("URL", Json.fromString(a.URL))
    )
  }
}
