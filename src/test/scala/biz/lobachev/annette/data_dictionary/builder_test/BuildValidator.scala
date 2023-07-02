package biz.lobachev.annette.data_dictionary.builder_test

import biz.lobachev.annette.data_dictionary.builder.model.Domain

trait BuildValidator {

  def validateAndProcess(res: Either[Seq[String], Domain])(process: Domain => Unit): Unit =
    res match {
      case Left(err)     =>
        err.foreach(println)
        throw new IllegalArgumentException(err.mkString("\n"))
      case Right(domain) => process(domain)
    }

}
