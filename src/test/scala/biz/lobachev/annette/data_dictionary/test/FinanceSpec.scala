package biz.lobachev.annette.data_dictionary.test

import biz.lobachev.annette.data_dictionary.builder.DomainBuilder
import biz.lobachev.annette.data_dictionary.test.finance.Finance
import biz.lobachev.annette.data_dictionary.generators.Generator
import biz.lobachev.annette.data_dictionary.generators.audit.AuditDDLGenerator
import biz.lobachev.annette.data_dictionary.generators.click_house_ddl.ClickHouseDDLGenerator
import biz.lobachev.annette.data_dictionary.generators.dbml.DbmlGenerator
import biz.lobachev.annette.data_dictionary.generators.golang.{GoGenerator, GoGeneratorOptions, Gorm, Sqlx}
import biz.lobachev.annette.data_dictionary.generators.kotlin.KotlinGenerator
import biz.lobachev.annette.data_dictionary.generators.postgres_ddl.PostgresDDLGenerator
import biz.lobachev.annette.data_dictionary.generators.xls_data.ExcelDataTemplateGenerator
import biz.lobachev.annette.data_dictionary.generators.xls_domain.DomainExcelGenerator
import biz.lobachev.annette.data_dictionary.model.*
import org.scalatest.wordspec.AnyWordSpec

class FinanceSpec extends AnyWordSpec with BuildValidator {

  val domainRef = Finance.data
  val build     = DomainBuilder.build(domainRef)

  "Finance model" should {

    "generate Postgres DDL" in
      validateAndProcess(build) { domain =>
        Generator.generate(
          PostgresDDLGenerator(
            domain = domain,
            withNameComment = true,
            withDescriptionComment = true,
          ),
          s"examples/${domain.id}/ddl",
        )
      }

    "generate audit DDL" in
      validateAndProcess(build) { domain =>
        Generator.generate(
          AuditDDLGenerator(
            domain = domain,
          ),
          s"examples/${domain.id}/ddl",
        )
      }

    "generate ClickHouse DDL" in
      validateAndProcess(build) { domain =>
        Generator.generate(
          ClickHouseDDLGenerator(
            domain = domain,
          ),
          s"examples/${domain.id}/ddl",
        )
      }

    "generate domain in Excel for Go" in
      validateAndProcess(build) { domain =>
        val domain1 = domain
        val domain2 = domain
        Generator.generate(
          DomainExcelGenerator(
            target = GoTarget,
            filename = "go-finance-domain",
            translation = Map.empty,
            domains = domain1,
            domain2,
          ),
          s"examples/${domain.id}",
        )
      }

    "generate domain in Excel for Kotlin" in
      validateAndProcess(build) { domain =>
        Generator.generate(
          DomainExcelGenerator(
            KotlinTarget,
            "kotlin-finance-domain",
            Map.empty,
            domain,
          ),
          s"examples/${domain.id}",
        )
      }

    "generate Kotlin tables" in
      validateAndProcess(build) { domain =>
        Generator.generate(
          KotlinGenerator(
            domain = domain,
          ),
          s"examples/${domain.id}/kotlin",
        )
      }

    "generate Go Gorm code" in
      validateAndProcess(build) { domain =>
        Generator.generate(
          GoGenerator(
            domain,
            GoGeneratorOptions(Gorm()),
          ),
          s"examples/${domain.id}/go_gorm",
        )
      }

    "generate Go Sqlx code" in
      validateAndProcess(build) { domain =>
        Generator.generate(
          GoGenerator(
            domain,
            GoGeneratorOptions(Sqlx),
          ),
          s"examples/${domain.id}/go_sqlx",
        )
      }

    "generate Excel data template" in
      validateAndProcess(build) { domain =>
        Generator.generate(
          ExcelDataTemplateGenerator(
            domain,
          ),
          s"examples/${domain.id}/xls_data",
        )
      }

    "generate dbml diagram" in
      validateAndProcess(build) { domain =>
        Generator.generate(
          DbmlGenerator(
            domain,
          ),
          s"examples/${domain.id}",
        )
      }
  }
}
