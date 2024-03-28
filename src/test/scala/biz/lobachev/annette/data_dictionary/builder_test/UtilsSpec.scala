package biz.lobachev.annette.data_dictionary.builder_test

import biz.lobachev.annette.data_dictionary.builder.utils.Utils
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UtilsSpec extends AnyWordSpec with Matchers {

  "Utils" should {
    "find duplicates" in {
      val duplicates = Utils.findDuplicates(Seq("a", "b", "c", "a", "c"))
      duplicates should equal( Seq("a", "c"))
    }

  }

}
