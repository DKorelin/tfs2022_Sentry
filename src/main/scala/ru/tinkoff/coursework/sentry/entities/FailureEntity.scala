package ru.tinkoff.coursework.sentry.entities

import io.circe.Decoder.Result
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.{Decoder, Encoder, HCursor, Json}

import java.sql.Timestamp

case class FailureEntity(failureId: Long, URL: String, description: String, timestamp: Timestamp)

object FailureEntity {
  implicit val TimestampFormat: Encoder[Timestamp] with Decoder[Timestamp] = new Encoder[Timestamp] with Decoder[Timestamp] {
    override def apply(a: Timestamp): Json = Encoder.encodeLong.apply(a.getTime)

    override def apply(c: HCursor): Result[Timestamp] = Decoder.decodeLong.map(s => new Timestamp(s)).apply(c)
  }

  implicit val jsonDecoder: Decoder[FailureEntity] = new Decoder[FailureEntity] {
    final def apply(c: HCursor): Result[FailureEntity] = {
      for {
        failureId <- c.downField("failureId").as[Long]
        url <- c.downField("URL").as[String]
        description <- c.downField("description").as[String]
        timestamp <- c.downField("timestamp").as[Timestamp]
      } yield FailureEntity(failureId, url, description, timestamp)
    }
  }
  implicit val jsonEncoder: Encoder[FailureEntity] = new Encoder[FailureEntity] {
    final def apply(a: FailureEntity): Json = Json.obj(
      ("failureId", Json.fromLong(a.failureId)),
      ("URL", Json.fromString(a.URL)),
      ("description", Json.fromString(a.description)),
      ("timestamp", Json.fromLong(a.timestamp.getTime))
    )
  }
}