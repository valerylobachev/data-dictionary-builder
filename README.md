# Annette Data Dictionary builder

Annette Data Dictionary builder is the tool to define and generate data models and documentation. It uses DSL to define domain model elements 
such as data elements, enumerations, tables, data structures, indexes and relations. It has plugins (renderers) to generate:

* [x] SQL DDL script 
* [x] DbDiagram model definition (see [dbdiagram.io](https://dbdiagram.io/)). You can use DbDiagram to 
visualize model tables and generate SQL DDL scripts for PostgreSQL & MySQL 
* [x] Data model documentation in Markdown format. English, Polish and Russian localizations are provided. 
* [x] Data model documentation in Excel file. 
* [x] Excel file to create SQL INSERT statements
* [x] Kotlin source code for Spring Boot
* [ ] Java source code for Spring Boot
* [ ] Scala source code for Slick
* [ ] Golang source code for Gorm

## Table of contents

* [Get started](#get-started)
* [Examples](#examples)
* [License](#license)
* [Legal](#legal)

## Get started

Create new Scala project and add Data Dictionary builder library to `build.sbt`:

```sbt
libraryDependencies += "biz.lobachev.annette" %% "data-dictionary-builder" % "0.3.2"
```

Create model definition `Simple.scala`:

```scala
package biz.lobachev.annette.data_dictionary.builder_test.simple

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.model._

object Simple {

  val simpleDomain = domain("Simple", "Simple Example", "This example provides simple person group data model")
          .withDataElements(
            // format: off
            dataElement("PersonId", "personId", IntInt(), "Person Id"),
            dataElement("GroupId", "groupId", IntInt(), "Group Id")
            // format: on
          )
          .withComponents(
            component("Shared", "Shared data structures")
                    .withEntities(
                      embeddedEntity("Modification", "Modification data structure")
                              .withFields(
                                // format: off
                                "updatedBy" :# StringVarchar(20) :@ "User updated record",
                                "updatedAt" :# InstantTimestamp() :@ "Timestamp of record update"
                                // format: on
                              )
                    ),
            component("PersonGroup", "Person Group Model")
                    withEntities(
                    tableEntity("Person", "Person")
                            .withPK(
                              "id" :#++ "PersonId"
                            )
                            .withFields(
                              // format: off
                              "firstname" :# StringVarchar(40) :@ "Person first name",
                              "lastname" :# StringVarchar(40) :@ "Person last name",
                              include("Modification")
                              // format: on
                            )
                            .withIndexes(
                              index("lastnameFirstname", "Search index by lastname and firstname", "lastname", "firstname")
                            ),
                    tableEntity("Group", "Group")
                            .withPK(
                              "id" :#++ "GroupId"
                            )
                            .withFields(
                              // format: off
                              "name" :# StringVarchar(100) :@ "Group name",
                              include("Modification")
                              // format: on
                            ),
                    tableEntity("GroupMember", "Group member")
                            .withPK(
                              "id" :#++ IntInt() :@ " Group member id"
                            )
                            .withFields(
                              // format: off
                              "groupId" :# "GroupId",
                              "personId" :# "PersonId",
                              include("Modification")
                              // format: on
                            )
                            .withIndexes(
                              uniqueIndex("personGroupIds", "Person id in each group must be unique", "groupId", "personId")
                            )
                            .withRelations(
                              manyToOneRelation("groupId", "Relation to groups", "Group", "groupId" -> "id"),
                              manyToOneRelation("personId", "Relation to persons", "Person", "personId" -> "id")
                            ),
            )
          )

}
```

Create application code to generate data model `DataDictionaryBuilderApp.scala`:

```scala
package ddbapp

import biz.lobachev.annette.data_dictionary.builder.rendering.Generator
import biz.lobachev.annette.data_dictionary.builder.rendering.dbdiagram.DbDiagramRenderer
import biz.lobachev.annette.data_dictionary.builder.rendering.kotlin.KotlinRenderer
import biz.lobachev.annette.data_dictionary.builder.rendering.xls_domain.{ExcelDomainRenderer, WorkbookTranslation}
import biz.lobachev.annette.data_dictionary.builder.rendering.xls_insert.ExcelInsertTemplateRenderer
import biz.lobachev.annette.data_dictionary.builder.rendering.markdown.{
  MarkdownRenderer,
  PolishTranslaltion,
  RussianTranslaltion
}

object DataDictionaryBuilderApp extends App {

  Simple.simpleDomain.build() match {
    case Left(err)     => err.foreach(println)
    case Right(domain) =>
      Generator.generate(DbDiagramRenderer(domain), s"docs/${domain.id}/")
      Generator.generate(KotlinRenderer(domain), s"docs/${domain.id}/kotlin/")
      Generator.generate(ExcelDomainRenderer(domain, WorkbookTranslation.ru), s"docs/${domain.id}")
      Generator.generate(ExcelInsertTemplateRenderer(domain), s"docs/${domain.id}/template/")
      Generator.generate(MarkdownRenderer(domain), s"docs/${domain.id}/")
      Generator.generate(MarkdownRenderer(domain, PolishTranslaltion), s"docs/${domain.id}/")
      Generator.generate(MarkdownRenderer(domain, RussianTranslaltion), s"docs/${domain.id}/")
  }

}
```

Run code generation `sbt run`. After code generation completes you will have the following files:

```
└── docs
    └── Simple
        ├── db_diagram.dbml  - DbDiagram definition
        ├── simple_en.md     - markdown documentation in EN
        ├── simple_pl.md     - markdown documentation in PL
        └── simple_ru.md     - markdown documentation in RU
```

## Examples

To understand DSL check the following examples

* Simple - data model provides simple person group model 
  (see [model](src/test/scala/biz/lobachev/annette/data_dictionary/builder_test/simple/Simple.scala) and 
  [generator](src/test/scala/biz/lobachev/annette/data_dictionary/builder_test/SimpleSpec.scala)). It generates [DbDiagram](docs/Simple/db_diagram.dbml)
  and markdown docs in [EN](docs/Simple/simple_en.md), [PL](docs/Simple/simple_pl.md) and [RU](docs/Simple/simple_ru.md)

* Store - data model of simple store that provides all DSL features
  (see [model](src/test/scala/biz/lobachev/annette/data_dictionary/builder_test/store/Store.scala) and
  [generator](src/test/scala/biz/lobachev/annette/data_dictionary/builder_test/StoreSpec.scala)). It generates [DbDiagram](docs/Store/db_diagram.dbml)
  and markdown docs in [EN](docs/Store/store_en.md), [PL](docs/Store/store_pl.md) and [RU](docs/Store/store_ru.md)

* Finance - complex data model provides tables and data structures similar to SAP ERP Finance
  (see [model](src/test/scala/biz/lobachev/annette/data_dictionary/builder_test/finance/Finance.scala) and
  [generator](src/test/scala/biz/lobachev/annette/data_dictionary/builder_test/FinanceSpec.scala)). It generates [DbDiagram](docs/Finance/db_diagram.dbml)
  and markdown docs in [EN](docs/Finance/finance_en.md), [PL](docs/Finance/finance_pl.md) and [RU](docs/Finance/finance_ru.md)


## License

Annette Data Dictionary builder is Open Source and available under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

## Legal

Copyright 2023 Valery Lobachev. All rights reserved.
