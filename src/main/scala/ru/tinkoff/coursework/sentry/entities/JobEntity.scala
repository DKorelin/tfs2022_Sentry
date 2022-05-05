package ru.tinkoff.coursework.sentry.entities

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

import java.sql.Timestamp

case class JobEntity(jobId: Long, serviceId: Long, description: String, startTime: Timestamp, endTime: Timestamp)

object JobEntity {
  implicit val TimestampFormat: Encoder[Timestamp] with Decoder[Timestamp] = new Encoder[Timestamp] with Decoder[Timestamp] {
    override def apply(a: Timestamp): Json = Encoder.encodeLong.apply(a.getTime)

    override def apply(c: HCursor): Result[Timestamp] = Decoder.decodeLong.map(s => new Timestamp(s)).apply(c)
  }
  implicit val jsonJobEntityDecoder: Decoder[JobEntity] = new Decoder[JobEntity] {
    final def apply(c: HCursor): Result[JobEntity] = {
      for {
        jobId <- c.downField("jobId").as[Long]
        serviceId <- c.downField("serviceId").as[Long]
        description <- c.downField("description").as[String]
        startTime <- c.downField("startTime").as[Timestamp]
        endTime <- c.downField("endTime").as[Timestamp]
      } yield JobEntity(jobId, serviceId, description, startTime, endTime)
    }
  }
  implicit val jsonJobEntityEncoder: Encoder[JobEntity] = new Encoder[JobEntity] {
    final def apply(a: JobEntity): Json = Json.obj(
      ("jobId", Json.fromLong(a.serviceId)),
      ("serviceId", Json.fromLong(a.serviceId)),
      ("description", Json.fromString(a.description)),
      ("startTime", Json.fromLong(a.startTime.getTime)),
      ("endTime", Json.fromLong(a.endTime.getTime))
    )
  }
}
