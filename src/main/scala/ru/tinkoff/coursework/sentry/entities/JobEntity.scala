package ru.tinkoff.coursework.sentry.entities

import io.circe.Decoder.Result
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, HCursor, Json}
import java.sql.Timestamp

case class JobEntity(jobId: Long, serviceId: Long, description: String, startTime: Timestamp, endTime: Timestamp)

object JobEntity {
  implicit val TimestampFormat: Encoder[Timestamp] with Decoder[Timestamp] = new Encoder[Timestamp] with Decoder[Timestamp] {
    override def apply(a: Timestamp): Json = Encoder.encodeLong.apply(a.getTime)

    override def apply(c: HCursor): Result[Timestamp] = Decoder.decodeLong.map(s => new Timestamp(s)).apply(c)
  }
  implicit val jsonJobEntityDecoder: Decoder[JobEntity] = deriveDecoder

  implicit val jsonJobEntityEncoder: Encoder[JobEntity] = deriveEncoder
}
