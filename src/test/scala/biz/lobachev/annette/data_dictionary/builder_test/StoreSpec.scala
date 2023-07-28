package biz.lobachev.annette.data_dictionary.builder_test

import biz.lobachev.annette.data_dictionary.builder.rendering.Generator
import biz.lobachev.annette.data_dictionary.builder.rendering.dbdiagram.DbDiagramRenderer
import biz.lobachev.annette.data_dictionary.builder.rendering.kotlin.KotlinRenderer
import biz.lobachev.annette.data_dictionary.builder.rendering.markdown.{
  MarkdownRenderer,
  PolishTranslaltion,
  RussianTranslaltion,
}
import biz.lobachev.annette.data_dictionary.builder_test.store.Store
import org.scalatest.wordspec.AnyWordSpec
import biz.lobachev.annette.data_dictionary.builder.rendering.xls.ExcelInsertTemplateRenderer

class StoreSpec extends AnyWordSpec with BuildValidator {

  val build = Store.storeDomain.build()

  "Store model" should {
    "generate DB Diagram" in {
      validateAndProcess(build) { domain =>
        Generator.generate(
          DbDiagramRenderer(domain),
          s"docs/${domain.id}/",
        )
      }
    }

    "generate Kotlin entities" in {
      validateAndProcess(build) { domain =>
        Generator.generate(
          KotlinRenderer(domain),
          s"docs/${domain.id}/kotlin/",
        )
      }
    }

    "generate Excel INSERT template" in {
      validateAndProcess(build) { domain =>
        Generator.generate(
          ExcelInsertTemplateRenderer(domain),
          s"docs/${domain.id}/template/",
        )
      }
    }

    "generate documentation in EN" in {
      validateAndProcess(build) { domain =>
        Generator.generate(
          MarkdownRenderer(domain),
          s"docs/${domain.id}/",
        )
      }
    }

    "generate documentation in RU" in {
      validateAndProcess(build) { domain =>
        Generator.generate(
          MarkdownRenderer(domain, RussianTranslaltion),
          s"docs/${domain.id}/",
        )
      }
    }

    "generate documentation in PL" in {
      validateAndProcess(build) { domain =>
        Generator.generate(
          MarkdownRenderer(domain, PolishTranslaltion),
          s"docs/${domain.id}/",
        )
      }
    }
  }

}
