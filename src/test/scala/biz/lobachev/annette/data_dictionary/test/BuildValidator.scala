package biz.lobachev.annette.data_dictionary.test

import biz.lobachev.annette.data_dictionary.builder.{BuildResult, Message}
import biz.lobachev.annette.data_dictionary.model.Domain

trait BuildValidator {
  val VALIDATION_FAILED = "validation failed"

  def validateAndProcess(res: Either[Seq[Message], BuildResult])(process: Domain => Unit): Unit =
    res match {
      case Left(messages) =>
        Message.printMessages(messages)
        throw new IllegalArgumentException(VALIDATION_FAILED)
      case Right(res)     =>
        Message.printMessages(res.warnings)
        process(res.domain)
    }

}
