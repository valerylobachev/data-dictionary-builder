package biz.lobachev.annette.data_dictionary.builder_test

import biz.lobachev.annette.data_dictionary.builder.model.RawDomain

trait BuildValidator {

  def validateAndProcess(res: Either[Seq[String], RawDomain])(process: RawDomain => Unit): Unit =
    res match {
      case Left(err)     =>
        err.foreach(println)
        println()
        println(s"Total errors: ${err.length}")
        throw new IllegalArgumentException(err.mkString("\n"))
      case Right(domain) => process(domain)
    }

}
