package ru.tinkoff.coursework.sentry.entities

import io.circe.Decoder.Result
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, HCursor, Json}
import java.sql.Timestamp

case class FailureEntity(URL: String, description: String, timestamp: Timestamp)

object FailureEntity {
  implicit val TimestampFormat: Encoder[Timestamp] with Decoder[Timestamp] = new Encoder[Timestamp] with Decoder[Timestamp] {
    override def apply(a: Timestamp): Json = Encoder.encodeLong.apply(a.getTime)

    override def apply(c: HCursor): Result[Timestamp] = Decoder.decodeLong.map(s => new Timestamp(s)).apply(c)
  }
  implicit def failureEventEncode: Decoder[FailureEntity]= deriveDecoder

  implicit def failureEventDecoder: Encoder[FailureEntity] = deriveEncoder
}