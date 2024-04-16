package biz.lobachev.annette.data_dictionary.builder_test

import biz.lobachev.annette.data_dictionary.builder.builder.Builder
import biz.lobachev.annette.data_dictionary.builder_test.finance.Finance
import biz.lobachev.annette.data_dictionary.builder_test.simple.Simple
import biz.lobachev.annette.data_dictionary.builder_test.store.Store
import org.scalatest.wordspec.AnyWordSpec

class BuilderSpec extends AnyWordSpec {

  "New builder" should {
    "build Finance" in {
      val builder = new Builder(Finance.data)
      validate(builder)
    }

    "build Simple" in {
      val builder = new Builder(Simple.data)
      validate(builder)
    }

    "build Store" in {
      val builder = new Builder(Store.data)
      validate(builder)
    }

  }

  private def validate(builder: Builder): Unit =
    builder.build() match {
      case Left(err)     =>
        err.foreach(println)
        println()
        println(s"Total errors: ${err.length}")
        throw new IllegalArgumentException(err.mkString("\n"))
      case Right(domain) =>
        println(s"Done building domain: ${domain.id} ")
    }
}
