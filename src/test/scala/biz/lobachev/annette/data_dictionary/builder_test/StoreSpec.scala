package biz.lobachev.annette.data_dictionary.builder_test

import biz.lobachev.annette.data_dictionary.builder.builder.DomainBuilder
import biz.lobachev.annette.data_dictionary.builder.rendering.Generator
import biz.lobachev.annette.data_dictionary.builder.rendering.dbdiagram.DbDiagramRenderer
import biz.lobachev.annette.data_dictionary.builder.rendering.`export`.ExportJsonRenderer
import biz.lobachev.annette.data_dictionary.builder.rendering.ddl.DDLRenderer
import biz.lobachev.annette.data_dictionary.builder.rendering.kotlin.KotlinRenderer
import biz.lobachev.annette.data_dictionary.builder.rendering.markdown.{
  MarkdownRenderer,
  PolishTranslaltion,
  RussianTranslaltion,
}
import biz.lobachev.annette.data_dictionary.builder.rendering.xls_domain.{ExcelDomainRenderer, WorkbookTranslation}
import biz.lobachev.annette.data_dictionary.builder_test.store.Store
import org.scalatest.wordspec.AnyWordSpec
import biz.lobachev.annette.data_dictionary.builder.rendering.xls_insert.{
  ExcelInsertTemplateRenderer,
  ExcelInsertTemplateTranslation,
}

class StoreSpec extends AnyWordSpec with BuildValidator {

  val build = Store.data.build()

  "Store model" should {
    "generate DDL" in {
      validateAndProcess(build) { domain =>
        Generator.generate(
          DDLRenderer(domain),
          s"docs/${domain.id}/",
        )
      }
    }

    "generate physical DB Diagram" in {
      validateAndProcess(build) { domain =>
        Generator.generate(
          DbDiagramRenderer(domain),
          s"docs/${domain.id}/",
        )
      }
    }

    "generate logical DB Diagram" in {
      validateAndProcess(build) { domain =>
        Generator.generate(
          DbDiagramRenderer(domain, true),
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

    "generate Excel domain" in {
      validateAndProcess(build) { domain =>
        Generator.generate(
          ExcelDomainRenderer(domain, WorkbookTranslation.ru),
          s"docs/${domain.id}",
        )
      }
    }

    "generate Excel INSERT template EN" in {
      validateAndProcess(build) { domain =>
        Generator.generate(
          ExcelInsertTemplateRenderer(domain),
          s"docs/${domain.id}/template_en/",
        )
      }
    }

    "generate Excel INSERT template RU" in {
      validateAndProcess(build) { domain =>
        Generator.generate(
          ExcelInsertTemplateRenderer(domain, ExcelInsertTemplateTranslation.RU),
          s"docs/${domain.id}/template_ru/",
        )
      }
    }

    "export to JSON" in {
      validateAndProcess(DomainBuilder.buildStage1(Store.data)) { domain =>
        Generator.generate(
          ExportJsonRenderer(domain),
          s"docs/${domain.id}/",
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
