package biz.lobachev.annette.data_dictionary.test.order

import biz.lobachev.annette.data_dictionary.builder.DomainBuilder
import biz.lobachev.annette.data_dictionary.generators.Generator
import biz.lobachev.annette.data_dictionary.generators.click_house_ddl.ClickHouseDDLGenerator
import biz.lobachev.annette.data_dictionary.generators.dbml.DbmlGenerator
import biz.lobachev.annette.data_dictionary.generators.golang.{GoGenerator, GoGeneratorOptions, Gorm, Sqlx}
import biz.lobachev.annette.data_dictionary.generators.kotlin.KotlinGenerator
import biz.lobachev.annette.data_dictionary.generators.postgres_ddl.PostgresDDLGenerator
import biz.lobachev.annette.data_dictionary.generators.xls_data.ExcelDataTemplateGenerator
import biz.lobachev.annette.data_dictionary.generators.xls_domain.DomainExcelGenerator
import biz.lobachev.annette.data_dictionary.model.*

object Order extends App {

  val build = DomainBuilder.build(OrderDomain.data)

  build match {
    case Left(messages) =>
      messages.foreach(println)
    case Right(result)  =>
      result.warnings.foreach(println)
      val domain = result.domain
      Generator.generate(PostgresDDLGenerator(domain), s"examples/${domain.id}/ddl")
      Generator.generate(ClickHouseDDLGenerator(domain), s"examples/${domain.id}/ddl")
      Generator.generate(
        DomainExcelGenerator(
          GoTarget,
          "go-domain",
          Map.empty,
          domain,
        ),
        s"examples/${domain.id}",
      )
      Generator.generate(
        DomainExcelGenerator(
          KotlinTarget,
          "kotlin-domain",
          Map.empty,
          domain,
        ),
        s"examples/${domain.id}",
      )
      Generator.generate(KotlinGenerator(domain), s"examples/${domain.id}/kotlin")
      Generator.generate(GoGenerator(domain, GoGeneratorOptions(Gorm())), s"examples/${domain.id}/go_gorm")
      Generator.generate(GoGenerator(domain, GoGeneratorOptions(Sqlx)), s"examples/${domain.id}/go_sqlx")
      Generator.generate(ExcelDataTemplateGenerator(domain), s"examples/${domain.id}/xls_data")
      Generator.generate(DbmlGenerator(domain), s"examples/${domain.id}")

  }
}
