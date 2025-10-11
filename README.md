<div align="center">
  <img width="400" alt="Annette Data Dictionary Logo" src="docs/logo.png"/>
  <h1>Annette Data Dictionary</h1>
  <h3>The Single Source of Truth for Your Data Model: Description, Code Generation, and Documentation</h3>
</div>

<p align="center">
  🌍 <a href="README_RU.md">Read in Russian</a>
</p>

## 🚀 Why Do You Need This?

Tired of maintaining consistency between SQL scripts, application code, and documentation? **Annette Data Dictionary**
solves this problem.

**Describe your data model once using Scala DSL** — and automatically generate everything you need:

* ✅ **SQL DDL Scripts** (PostgreSQL, ClickHouse)
* ✅ **Entity Code** (Kotlin, Go for GORM and SQLx)
* ✅ **Technical Documentation** in Excel
* ✅ **Database Schemas** in DBML format for [dbdiagram.io](https://dbdiagram.io)
* ✅ **Excel Templates** for data entry and import

> **Perfect for teams:** A once-defined model becomes the **Single Source of Truth** for architects, analysts, and
> developers.

## Production Use

Annette Data Dictionary is battle-tested and used in real projects.  
For example, in a large microservice system for equipment maintenance and repair management:

- 21 databases
- over 600 tables
- automatic code and documentation generation

The data model became the **Single Source of Truth**, simplifying collaboration between architects, analysts, and
developers.

## Table of contents

<!-- TOC -->

* [Getting Started](#getting-started)
* [Examples](#examples)
* [Documentation](#documentation)
* [License](#license)

<!-- TOC -->


## Getting Started

It will take you **5-10 minutes** to describe a simple model and generate artifacts from it.

### Example Schema

Let's consider the domain model of a simple order management system:

- `materials` — materials catalog
- `customers` — customers catalog
- `orders` — customer order (linked to customer)
- `order_lines` — order line items (linked to materials)

Schema:

![orders-dbml.png](docs/orders-dbml.png)

### Step-by-Step Guide

**Step 1: Add Dependency**

Add Annette Data Dictionary to your `build.sbt`:

```sbt
libraryDependencies += "biz.lobachev.annette" %% "data-dictionary-builder" % "0.5.0"
```

**Step 2: Describe Data Model**

Create a Scala file with domain description. Here's a shortened example:

```scala
object OrderDomain {

  val data = domain("Order", "Order model", "Model for simple order management system.",
  )
    .withLabels(
      goTablePackage("github.com/valerylobachev/order/entity"),
      // ...
    )
    .withEnums(
      // ...
    )
    .withDataElements(
      "MaterialId" :! "materialId" :# varchar(20) :@ "Material Id",
      // ...
    )
    .withTables(
      // ...
      table("Order", "Order")
        .withPK(
          "id" :# "OrderId",
        )
        .withFields(
          "orderDate" :# date :@ "Date of order" :== "CURRENT_DATE",
          dataElementTableField("CustomerId"),
          // ...
        )
        .withIndexes(
          index("status", "Index to filter orders by status", "status"),
          // ...
        )
        .withRelations(
          manyToOne("customerId", "Reference to customer", "Customer", "customerId" -> "id")
            .withAssociation("customer", "orders"),
          // ...
        ),
      // ...
    )
    .withStructs(
      // ...
      struct("OrderDto", "Order DTO")
        .likeTable("Order")
        .withFields(
          "totalAmount" :## decimal(9, 2) :@ "Total amount of order",
          "customer" :##? linkedStruct("CustomerDto") :@ "Customer master data",
          "lines" :## linkedStructArray("OrderLineDto") :@ "Order lines",
        ),
      // ...
    )
}
```

*See full code in [OrderDomain.scala example](src/test/scala/biz/lobachev/annette/data_dictionary/test/order/OrderDomain.scala).*

**Step 3: Write Generator Code**

Create a main class to build the model and run generators.

```scala
object Order extends App {

  val build = DomainBuilder.build(OrderDomain.data)

  build match {
    case Left(messages) =>
      messages.foreach(println)
    case Right(result) =>
      result.warnings.foreach(println)
      val domain = result.domain
      Generator.generate(PostgresDDLGenerator(domain), s"examples/${domain.id}/ddl")
    // ...
  }
}
```

*See full code in [Order.scala example](src/test/scala/biz/lobachev/annette/data_dictionary/test/order/Order.scala).*

**Step 4: Run Generation**

Execute in command line:

```bash
sbt run biz.lobachev.annette.data_dictionary.test.order.Order
```

**Step 5: Get Artifacts**

Generated files will appear in examples/Order folder:

    ```
    Order/
    ├── ddl                       - DDL scripts  
    │   ├── click_house_ddl.sql   - DDL script for ClickHouse 
    │   └── postgres_ddl.sql      - DDL script for PostgreSQL 
    ├── go_gorm                   - Go source code for Gorm library
    ├── go_sqlx                   - Go source code for sqlx library
    ├── kotlin                    - Kotlin source code 
    ├── xls_data                  - Excel templates for data loading
    ├── Order.dbml                - Data model schema in DBML format
    ├── go-domain.xlsx            - Data domain description (with Go data types) in Excel format
    └── kotlin-domain.xlsx        - Data domain description (with Kotlin data types) in Excel format
    ```

## Examples

Explore ready-made examples to better understand DSL syntax:

| Example      | Description                                   | Links                                                                                                                                                                                                                           |
|-------------|--------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Order**   | Simple order model (4 tables)         | • [model](src/test/scala/biz/lobachev/annette/data_dictionary/test/order/OrderDomain.scala) <br>• [generation code](src/test/scala/biz/lobachev/annette/data_dictionary/test/order/Order.scala) <br>• [artifacts](examples/Order) |
| **Finance** | Complex model similar to SAP ERP Finance | • [model](src/test/scala/biz/lobachev/annette/data_dictionary/test/finance/Finance.scala) <br>• [generation code](src/test/scala/biz/lobachev/annette/data_dictionary/test/FinanceSpec.scala) <br>• [artifacts](examples/Finance) |


## Documentation

Detailed documentation with complete description of DSL capabilities and API is available in the [main guide](docs/index.md):

* **[Model Description Language (DSL)](docs/index.md#model-description-language-dsl)** — complete syntax reference.
* **[Model Building and Artifact Generation](docs/index.md#model-building-and-artifact-generation)** — how to configure and run generation.

## License

Annette Data Dictionary is distributed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

© 2023 Valery Lobachev. All rights reserved.
