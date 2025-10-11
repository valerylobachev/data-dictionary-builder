package biz.lobachev.annette.data_dictionary.test

import biz.lobachev.annette.data_dictionary.builder.DomainBuilder
import biz.lobachev.annette.data_dictionary.test.validation.{
  PostValidateErrors,
  PreValidateErrors,
}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ValidationSpec extends AnyWordSpec with Matchers with BuildValidator {
  "Validation" should {
    "pre-validate errors" in {
      val domainRef = PreValidateErrors.data
      val thrown    = intercept[IllegalArgumentException] {
        validateAndProcess(DomainBuilder.build(domainRef)) { domain => }
      }
      thrown.getMessage should include(VALIDATION_FAILED)
    }

    "post-validate errors" in {
      val domainRef = PostValidateErrors.data
      val thrown    = intercept[IllegalArgumentException] {
        validateAndProcess(DomainBuilder.build(domainRef)) { domain => }
      }
      thrown.getMessage should include(VALIDATION_FAILED)
    }
  }
}
