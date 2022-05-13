package ru.tinkoff.coursework.sentry.database

trait SentryDatabase extends FailureDAO
  with JobDAO
  with ServiceDAO
  with TagDAO
  with UserDAO
  with TelegramDAO
