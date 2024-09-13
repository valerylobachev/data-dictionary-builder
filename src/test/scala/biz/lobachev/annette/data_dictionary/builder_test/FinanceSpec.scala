package biz.lobachev.annette.data_dictionary.builder_test

import biz.lobachev.annette.data_dictionary.builder.builder.DomainBuilder
import biz.lobachev.annette.data_dictionary.builder.rendering.Generator
import biz.lobachev.annette.data_dictionary.builder.rendering.dbdiagram.DbDiagramRenderer
import biz.lobachev.annette.data_dictionary.builder.rendering.`export`.ExportJsonRenderer
import biz.lobachev.annette.data_dictionary.builder.rendering.ddl.DDLRenderer
import biz.lobachev.annette.data_dictionary.builder.rendering.golang.{GolangRenderer, Gorm, Sqlx}
import biz.lobachev.annette.data_dictionary.builder.rendering.kotlin.KotlinRenderer
import biz.lobachev.annette.data_dictionary.builder.rendering.markdown.{
  MarkdownRenderer,
  PolishTranslaltion,
  RussianTranslaltion,
}
import biz.lobachev.annette.data_dictionary.builder.rendering.xls_domain.{ExcelDomainRenderer, WorkbookTranslation}
import biz.lobachev.annette.data_dictionary.builder.rendering.xls_insert.{
  ExcelInsertTemplateRenderer,
}
import biz.lobachev.annette.data_dictionary.builder_test.finance.Finance
import org.scalatest.wordspec.AnyWordSpec

class FinanceSpec extends AnyWordSpec with BuildValidator {

  val build = Finance.data.build()

  "Finance model" should {
    "generate DDL" in {
      validateAndProcess(build) { domain =>
        Generator.generate(
          DDLRenderer(
            domain = domain,
            enableAudit = true,
          ),
          s"docs/${domain.id}/ddl",
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

    "generate Golang (Gorm) entities" in {
      validateAndProcess(build) { domain =>
        Generator.generate(
          GolangRenderer(domain, Gorm),
          s"docs/${domain.id}/go_gorm/",
        )
      }
    }

    "generate Golang (sqlx) entities" in {
      validateAndProcess(build) { domain =>
        Generator.generate(
          GolangRenderer(domain, Sqlx),
          s"docs/${domain.id}/go_sqlx/",
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

    "generate Excel INSERT template" in {
      validateAndProcess(build) { domain =>
        Generator.generate(
          ExcelInsertTemplateRenderer(domain),
          s"docs/${domain.id}/",
        )
      }
    }

    "export to JSON" in {
      validateAndProcess(DomainBuilder.buildStage1(Finance.data)) { domain =>
        Generator.generate(
          ExportJsonRenderer(domain),
          s"docs/${domain.id}/",
        )
      }
    }

    "generate documentation" in {
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
