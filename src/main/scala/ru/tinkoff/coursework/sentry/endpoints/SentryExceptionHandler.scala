package ru.tinkoff.coursework.sentry.endpoints

import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.ExceptionHandler
import ru.tinkoff.coursework.sentry.SentryException

object SentryExceptionHandler {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

  val exceptionHandler: ExceptionHandler =
    ExceptionHandler { case e: SentryException =>
      complete(BadRequest, ExceptionResponse(e.getMessage))
    }
}
