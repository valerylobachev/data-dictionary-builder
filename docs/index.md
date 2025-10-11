# Documentation

<!-- TOC -->
* [Model Description Language (DSL)](#model-description-language-dsl)
  * [Domain](#domain)
  * [Components](#components)
  * [Labels](#labels)
  * [Data Types](#data-types)
  * [Enumerations](#enumerations)
  * [Data Elements](#data-elements)
  * [Insertion Templates](#insertion-templates)
  * [Tables](#tables)
    * [Table Fields](#table-fields)
    * [Relationships](#relationships)
    * [Indexes](#indexes)
  * [Structures](#structures)
    * [Structure Fields](#structure-fields)
* [Model Building and Artifact Generation](#model-building-and-artifact-generation)
  * [DDL Scripts for PostgreSQL](#ddl-scripts-for-postgresql)
  * [DDL Scripts for ClickHouse](#ddl-scripts-for-clickhouse)
  * [Domain Model Description in Excel Format](#domain-model-description-in-excel-format)
  * [Domain Model Schema in DBML Format](#domain-model-schema-in-dbml-format)
  * [Excel Data Templates for Database Loading](#excel-data-templates-for-database-loading)
  * [Kotlin Source Code](#kotlin-source-code)
  * [Go Source Code](#go-source-code)
<!-- TOC -->

## Model Description Language (DSL)

The Annette Data Dictionary DSL allows you to declaratively describe all elements of your data model.

* 🏛️ [Domain](#domain): The root element describing the subject area.
* 📦 [Components](#components): Hierarchical grouping for decomposing large models.
* 🏷️ [Labels](#labels): Flexible mechanism for customization (e.g., overriding a field's data type, specifying Java/Go
  package).
* 📝 [Enumerations](#enumerations): Types with a fixed set of values.
* 🧩 [Data Elements](#data-elements): Named types with business semantics (e.g., EmailAddress, ProductSKU) that can be
  reused in different tables.
* 🧩 [Insertion Templates](#insertion-templates): Templates for reusing groups of fields in different tables and
  structures.
* 🗂️ [Tables](#tables): Description of tables, their fields, primary keys, indexes, and relationships with other tables.
* 📊 [Structures](#structures): Structures for DTOs and data operations. Can inherit fields from tables with the ability
  to exclude or include fields.

To describe a data model, you need to add the following imports:

```scala
import biz.lobachev.annette.data_dictionary.dsl.DSL.*
import biz.lobachev.annette.data_dictionary.labels.Audit.* // optional
import biz.lobachev.annette.data_dictionary.labels.ClickHouse.* // optional
import biz.lobachev.annette.data_dictionary.labels.Go.* // optional
import biz.lobachev.annette.data_dictionary.labels.Java.* // optional
import biz.lobachev.annette.data_dictionary.labels.TablePrefixSuffix.* // optional
import biz.lobachev.annette.data_dictionary.labels.Postgres.* // optional
```

If you are using a domain model created in previous versions of Annette Data Dictionary (< `0.5.0`), then add the
CompatDSL import, which provides partial compatibility:

```scala
import biz.lobachev.annette.data_dictionary.dsl.CompatDSL.*
```

### Domain

**Domain** is the root container that describes your entire subject area. It contains all other model elements (tables,
enumerations, components, etc.).

📝 **Declaration Syntax**

The domain is declared as a variable in Scala. This variable is then used to build the final model.

```scala
val myDomain = domain("id", "name", "description")
  .withLabels(/* domain labels */)
  .withEnums(/* domain enumerations */)
  .withDataElements(/* domain data elements */)
  .withComponents(/* nested components */)
  .withTables(/* domain tables */)
  .withStructs(/* data models */)
```

⚙️ **Arguments and Parameters**

| Parameter     | Description                                        | Mandatory |
|---------------|----------------------------------------------------|-----------|
| `id`          | Unique domain identifier                           | ✅         |
| `name`        | Brief domain description                           | ✅         |
| `description` | Detailed description (optional, can be multi-line) | ❌         |

🧩 **Configuration Methods**

| Method                | Purpose            | Optional |
|-----------------------|--------------------|----------|
| `.withLabels`         | Domain labels      | ✅        |
| `.withEnums`          | Enumerations       | ✅        |
| `.withDataElements`   | Data elements      | ✅        |
| `.withComponents`     | Domain components  | ✅        |
| `.withTables`         | Domain tables      | ✅        |
| `.withStructs`        | Data structures    | ✅        |
| `.withLabeledStructs` | Labeled structures | ✅        |

💡 **Recommendations**

* If the model is small, all elements can be placed in a single file.
* For large models (tens or hundreds of tables), it is recommended to **decompose the domain into components**:

  ```scala
  .withComponents(
    financeComponent,
    logisticsComponent
  )
  ```

  This facilitates maintenance and allows distributing responsibility among teams.

> 🔗 See also: [Components](#components)

### Components

Components allow **decomposing the domain model** into separate parts.
They can contain the same elements as a domain: nested components, labels, enumerations, tables, and structures.

📘 **Example**

```scala
val comp = component("id", "name", "description")
  .withLabels(/* labels */)
  .withEnums(/* enumerations */)
  .withDataElements(/* data elements */)
  .withComponents(/* nested components */)
  .withTables(/* tables */)
  .withStructs(/* structures */)
  .withLabeledStructs(/* structure labels */)(/* structures */)
```

⚙️ **Features**

* Components **can include other components**, forming a hierarchy.
* Elements defined in one component (e.g., `enum` or `dataElement`) can be used in other components of the same domain.
* Components **do not impose restrictions** on cross-use of elements.

### Labels

Labels (`labels`) are a **flexible mechanism for customizing artifact generation**.
A label is a **key-value** pair:

```scala
postgresSchema("core")
```

📗 **Label Types**

| Type             | Where Applied                                                   | Example                     |
|------------------|-----------------------------------------------------------------|-----------------------------|
| **Direct**       | On a specific element (field, table)                            | `postgresDataType("uuid")`  |
| **Hierarchical** | At the element (table or structure), component, or domain level | `postgresSchema("finance")` |

🧭 **Hierarchical Label Lookup Mechanism**

When generating an artifact, the label is searched **from the bottom up**:

1. At the element level itself (e.g., table)
2. At the component level where the element is defined
3. At the parent component level
4. At the domain level
   If the label is not found, the **default value** is used
   (e.g., PostgreSQL schema `public`).

> 💡 **Example:**
> The `postgresSchema` label defines the PostgreSQL schema where the table will be created.
> If not explicitly set, the table will be placed in the `public` schema.

#### Audit Labels

Audit labels control the generation of database audit scripts. Labels are hierarchical.

| Label                | Description                                    | Example                |
|----------------------|------------------------------------------------|------------------------|
| `Audit.audit`        | Defines the audit table name                   | `Audit.audit("audit")` |
| `Audit.disableAudit` | Excludes the table and/or component from audit | `Audit.disableAudit()` |

#### ClickHouse Labels

ClickHouse labels control the generation of ClickHouse DDL scripts.

| Label                           | Description                                                                                                  | Example                                               |
|---------------------------------|--------------------------------------------------------------------------------------------------------------|-------------------------------------------------------|
| `ClickHouse.clickHouseEngine`   | Defines the ClickHouse engine to be used when creating the table. Hierarchical label. Default `MergeTree()`. | `ClickHouse.clickHouseEngine("ReplacingMergeTree()")` |
| `ClickHouse.clickHouseExclude`  | Excludes the table and/or component from ClickHouse DDL script generation. Hierarchical label.               | `ClickHouse.clickHouseExclude()`                      |
| `ClickHouse.clickHouseDataType` | Defines the field type for a table and/or data element. Direct label.                                        | `ClickHouse.clickHouseDataType("IPv4")`               |

#### PostgreSQL Labels

PostgreSQL labels control the generation of PostgreSQL DDL scripts.

| Label                       | Description                                                                                             | Example                              |
|-----------------------------|---------------------------------------------------------------------------------------------------------|--------------------------------------|
| `Postgres.postgresSchema`   | Defines the PostgreSQL schema to be used when creating the table. Hierarchical label. Default `public`. | `Postgres.postgresSchema("finance")` |
| `Postgres.postgresDataType` | Defines the field type for a table and/or data element. Direct label.                                   | `Postgres.postgresDataType("POINT")` |

#### Table Prefix/Suffix Labels

Table prefix/suffix labels control the generation of PostgreSQL and ClickHouse table names by adding a prefix or suffix
to the table name. Labels are hierarchical.

| Label                               | Description                 | Example                                      |
|-------------------------------------|-----------------------------|----------------------------------------------|
| `TablePrefixSuffix.tableNamePrefix` | Sets the table name prefix. | `TablePrefixSuffix.tableNamePrefix("fi")`    |
| `TablePrefixSuffix.tableNameSuffix` | Sets the table name suffix. | `TablePrefixSuffix.tableNameSuffix("users")` |

#### Go Labels

Go labels control the generation of Go table entity and structure code.

| Label                   | Description                                                                        | Example                                                                          |
|-------------------------|------------------------------------------------------------------------------------|----------------------------------------------------------------------------------|
| `Go.goTablePackage`     | Defines the package name for table entities. Hierarchical label. Default "model".  | `Go.goTablePackage("github.com/valerylobachev/finance/logic/repository/entity")` |
| `Go.goStructPackage`    | Defines the package name for structures. Hierarchical label. Default "data_model". | `Go.goStructPackage("github.com/valerylobachev/finance/logic/model")`            |
| `Go.goStructSourceFile` | Defines the file for structures. Hierarchical label.                               | `Go.goStructSourceFile("debtor")`                                                |
| `Go.goEnumPackage`      | Defines the package name for enumerations. Hierarchical label. Default "enums".    | `Go.goEnumPackage("github.com/valerylobachev/finance/logic/model")`              |
| `Go.goDataType`         | Defines the field type for a table and/or data element. Direct label.              | `Go.goDataType("complex64")`                                                     |

#### Java Labels

Java labels control the generation of Java/Scala/Kotlin table entity and structure code.

| Label                    | Description                                                                        | Example                                               |
|--------------------------|------------------------------------------------------------------------------------|-------------------------------------------------------|
| `Java.javaTablePackage`  | Defines the package name for table entities. Hierarchical label. Default "model".  | `Java.javaTablePackage("finance.data.fi.bp.entity"),` |
| `Java.javaStructPackage` | Defines the package name for structures. Hierarchical label. Default "data_model". | `Java.javaStructPackage("finance.data.fi.bp.model"),` |
| `Java.javaEnumPackage`   | Defines the package name for enumerations. Hierarchical label. Default "enums".    | `Java.javaEnumPackage("finance.data.fi.bp.model")`    |
| `Java.kotlinDataType`    | Defines the field type for a table and/or data element. Direct label.              | `Java.kotlinDataType("BigInteger")`                   |

#### Typescript Labels

Typescript labels control the generation of Typescript structure code.

| Label                        | Description                                                | Example                                                   |
|------------------------------|------------------------------------------------------------|-----------------------------------------------------------|
| `Typescript.tsStructPackage` | Defines the path to structure files. Hierarchical label.   | `Typescript.tsStructPackage("finance/data/fi/bp/model"),` |
| `Typescript.tsEnumPackage`   | Defines the path to enumeration files. Hierarchical label. | `Typescript.tsEnumPackage("finance/data/fi/bp/model")`    |

### Data Types

🔤 String Types:

| Data Type       | PostgreSQL | ClickHouse  | Golang | Kotlin |
|-----------------|------------|-------------|--------|--------|
| varchar(length) | varchar    | FixedString | string | String |
| char(length)    | char       | FixedString | string | String |
| string          | text       | String      | string | String |

🔢 Numeric Types

| Data Type                 | PostgreSQL       | ClickHouse | Golang          | Kotlin               |
|---------------------------|------------------|------------|-----------------|----------------------|
| goInt                     | bigint           | Int64      | int             | Long                 |
| smailint                  | smallint         | Int16      | int16           | Short                |
| integer                   | integer          | Int32      | int32           | Int                  |
| long                      | bigint           | Int64      | int64           | Long                 |
| smallSerial               | smallserial      | Int16      | int16           | Short                |
| serial                    | serial           | Int32      | int32           | Int                  |
| bigSerial                 | bigserial        | Int64      | int64           | Long                 |
| uint                      | ❌                | UInt64     | uint            | ❌                    |
| int8                      | ❌                | Int8       | int8            | Byte                 |
| uint8                     | ❌                | UInt8      | uint8           | ❌                    |
| uint16                    | ❌                | UInt16     | uint16          | ❌                    |
| uint32                    | ❌                | UInt32     | uint32          | ❌                    |
| uint64                    | ❌                | UInt64     | uint64          | ❌                    |
| decimal(scale, precision) | decimal          | Decimal    | decimal.Decimal | java.math.BigDecimal |
| float                     | real             | Float32    | float32         | Float                |
| double                    | double precision | Float64    | float64         | Double               |

🔲 Boolean Data Type:

| Data Type | PostgreSQL | ClickHouse | Golang | Kotlin  |
|-----------|------------|------------|--------|---------|
| bool      | boolean    | Bool       | bool   | Boolean |

⏰ Date and Time

| Data Type      | PostgreSQL  | ClickHouse    | Golang         | Kotlin                   | Notes                       |
|----------------|-------------|---------------|----------------|--------------------------|-----------------------------|
| instant        | timestamptz | DateTime64(9) | time.Time      | java.time.Instant        | Timestamp with time zone    |
| offsetDateTime | timestamptz | DateTime64(9) | time.Time      | java.time.OffsetDateTime | Timestamp with time zone    |
| dateTime       | timestamp   | DateTime      | civil.DateTime | java.time.LocalDateTime  | Timestamp without time zone |
| date           | date        | Date          | civil.Date     | java.time.LocalDate      | Date                        |
| time           | time        | Time          | civil.Time     | java.time.LocalTime      | Time                        |

📋 JSON and UUID Data Types:

| Data Type | PostgreSQL | ClickHouse | Golang | Kotlin | Notes                         |
|-----------|------------|------------|--------|--------|-------------------------------|
| json      | json       | JSON       | string | String | JSON in text representation   |
| jsonb     | jsonb      | JSON       | string | String | JSON in binary representation |
| uuid      | uuid       | uuid       | string | UUID   | UUID                          |

🗂 Enumeration Data Types:

To use enumerations, the syntax `enumeration(enumId)` is used, where `enumId` is the enumeration identifier.
The data type depends on the type of enumeration used:

| Enum Type  | PostgreSQL | ClickHouse  | Golang | Kotlin |
|------------|------------|-------------|--------|--------|
| NativeEnum | varchar    | FixedString | string | String |
| StringEnum | varchar    | FixedString | string | String |
| IntEnum    | integer    | Int32       | int32  | Int    |

🧩 Data Elements:

To use a data type from a data element, the syntax `dataElement(dataElementId)` is used, where `dataElementId` is the
data element identifier.
The data type is determined by the data type in the data element.

🧱 Data Types for Structures:

The data types listed below are only allowed in structures:

| Data Type                               | Golang                   | Kotlin                | Notes                                                                                                      |
|-----------------------------------------|--------------------------|-----------------------|------------------------------------------------------------------------------------------------------------|
| array(datatype)                         | []datatype               | List<datatype>        | Array of the specified data type                                                                           |
| set(datatype)                           | golang_set.Set[datatype] | Set<datatype>         | Set of the specified data type                                                                             |
| stringMap(datatype)                     | [string]datatype         | Map<String, datatype> | Map of the specified data type                                                                             |
| struct(structId)                        | object                   | object                | Object of structure `structId`                                                                             |
| linkedStruct(structId, [relation])      | object                   | object                | Object of structure `structId` (only for structures associated with tables and having relations)           |
| structArray(structId)                   | []object                 | List<object>          | Array of objects of structure `structId`                                                                   |
| linkedStructArray(structId, [relation]) | []object                 | List<object>          | Array of objects of structure `structId` (only for structures associated with tables and having relations) |

### Enumerations

Enumerations allow implementing data types with a fixed set of valid values.
The following types of enumerations are possible:

* Native - implemented at the PostgreSQL level
* String - implemented as a string field of a specified length `varchar(length)`
* Integer - implemented as an integer field

#### Native Enumeration

A native enumeration is defined as:

```scala
nativeEnum("id", "name", length, "description")
  .withValues(/* enumeration values */)
```

⚙️ Arguments and Parameters

| Parameter     | Description                                     | Mandatory |
|---------------|-------------------------------------------------|-----------|
| `id`          | Unique enumeration identifier                   | ✅         |
| `name`        | Brief enumeration description                   | ✅         |
| `length`      | Maximum number of characters in the enumeration | ✅         |
| `description` | Detailed enumeration description.               | ❌         |

Enumeration values are specified as: `<value> -> <constant name> -> <value description>`

Example of a native enumeration:

```scala
nativeEnum("LedgerType", "Ledger Type", 1)
  .withValues(
    "L" -> "LeadingLedger" -> "Leading ledger",
    "X" -> "ExtensionLedger" -> "Extension ledger",
  )
```

#### String Enumeration

A string enumeration is implemented as a string field of a specified length `varchar(length)`. A string enumeration can
be of the following type:

* Strict (`strictStringEnum`) - when assigning a value to a field, a validity check is performed
* Non-strict (`stringEnum`) - any value can be assigned to the field

Example:

```scala
stringEnum("LedgerType", "Ledger Type", 1)
  .withValues(
    "L" -> "LeadingLedger" -> "Leading ledger",
    "X" -> "ExtensionLedger" -> "Extension ledger",
  )
```

#### Integer Enumeration

An integer enumeration is implemented as an integer field. Like a string enumeration, it can be strict `strictIntEnum`
or non-strict `intEnum`. The enumeration value must be an integer value.

The enumeration is defined as:

```scala
intEnum("id", "name", "description")
  .withValues(/* enumeration values */)
```

⚙️ Arguments and Parameters

| Parameter     | Description                       | Mandatory |
|---------------|-----------------------------------|-----------|
| `id`          | Unique enumeration identifier     | ✅         |
| `name`        | Brief enumeration description     | ✅         |
| `description` | Detailed enumeration description. | ❌         |

Example:

```scala
intEnum("LedgerType", "Ledger Type")
  .withValues(
    "0" -> "LeadingLedger" -> "Leading ledger",
    "1" -> "ExtensionLedger" -> "Extension ledger",
  )
```

### Data Elements

Data Elements are named types with business semantics (e.g., CompanyCodeId, ProductSKU) that can be reused in tables and
structures.

A data element can be defined in two formats:

```scala
// new format
"id" :! "field name" :> "db field name" :# datatype :@ "name" :@@ "description" :* ( /* data element labels */ )

// old format
dataElement("id", "field name", datatype, "name")
  .withLabels(/* data element labels */)
  .withDbFieldName("db field name")
  .withDescription("description")

```

⚙️ Arguments and Parameters

| Parameter       | Description                    | Mandatory |
|-----------------|--------------------------------|-----------|
| `id`            | Unique data element identifier | ✅         |
| `field name`    | Field name                     | ✅         |
| `db field name` | Database field name            | ❌         |
| `datatype`      | Data type                      | ✅         |
| `name`          | Name (brief description)       | ✅         |
| `description`   | Detailed description           | ❌         |

Example:

```scala
// new data element format
"UserId" :! "userId" :# varchar(20) :@ "User Id" :@@ "description"


// old data element format
dataElement("CreditorId", "creditorId", varchar(10), "Creditor id")
  .withLabels(/* data element labels */)
  .withDescription("description")
```

### Insertion Templates

Insertion Templates (Includes) are designed for reusing groups of fields in tables and structures. If the same set of
fields is used in different tables, it can be extracted into an insertion template and used as `include("inclideId")` (
in table fields) or `includeStructFields("inclideId")` (in structure fields).

An insertion template definition has the following format:

```scala
includeDef("id", "name")
  .withDescription("description")
  .withFields(/* field descriptions */)
```

⚙️ Arguments and Parameters

| Parameter            | Description                                                                                               | Mandatory |
|----------------------|-----------------------------------------------------------------------------------------------------------|-----------|
| `id`                 | Unique insertion template identifier                                                                      | ✅         |
| `name`               | Name (brief description)                                                                                  | ✅         |
| `description`        | Detailed description                                                                                      | ❌         |
| `field descriptions` | List of template fields. The field description format is described in the [Fields](#table-fields) section | ✅         |

Example template for record modification fields:

```scala
includeDef("Modification", "Modification data structure")
  .withFields(
    "updatedBy" :# "UserId" :@ "User updated record",
    "updatedAt" :# instant :@ "Timestamp of record update" :== "CURRENT_TIMESTAMP"
  )
```

### Tables

Tables are a key element of the data model. They are described in the following format:

```scala
table("id", "name", "entity name")
  .withTableName("db table name")
  .withDescription("description")
  .withLabels(/* table labels */)
  .withPK(/* primary key field definitions */)
  .withFields(/* field definitions */)
  .withIndexes(/* index definitions */)
  .withRelations(/* relationship definitions */)
```

⚙️ **Arguments and Parameters**

| Parameter       | Description                                                                                               | Mandatory |
|-----------------|-----------------------------------------------------------------------------------------------------------|-----------|
| `id`            | Unique table identifier. Converted to PascalCase.                                                         | ✅         |
| `name`          | Name (brief description) of the table.                                                                    | ✅         |
| `entity name`   | Class (structure) name of the entity. If not specified, taken from `id`.                                  | ❌         |
| `db table name` | Table name. If not specified, the entity name (`entity name`) converted to plural and snake_case is used. | ❌         |
| `description`   | Detailed description (optional, can be multi-line).                                                       | ❌         |

🧩 **Configuration Methods**

| Method           | Purpose                                                             | Optional |
|------------------|---------------------------------------------------------------------|----------|
| `.withLabels`    | Table labels                                                        | ✅        |
| `.withPK`        | Describes [primary key fields](#table-fields)                       | ✅        |
| `.withFields`    | Describes [table fields](#table-fields)                             | ✅        |
| `.withRelations` | Describes [relationships](#relationships) (one-to-one, many-to-one) | ✅        |
| `.withIndexes`   | Describes [indexes](#indexes)                                       | ✅        |

Example table definition:

```scala
.withTables(
  table("Country", "Country", "Country")
    .withPK(
      "id" :# "CountryId",
    )
    .withFields(
      "name" :# "Name",
      "key" :#? varchar(3) :@ "Country Key",
      dataElementTableField("LanguageId"),
      include("Modification")
    )
    .withIndexes(
      uniqueIndex("countryKey", "Country key must be unique", "key"),
    )
    .withRelations(
      manyToOne("languageId", "Reference to language", "Language", "languageId" -> "id")
        .withAssociation("language"),
    )
```

#### Table Fields

Table fields are specified in the primary key sections (method `.withPK`) and in the field sections (method
`.withFields`) of the table definition. The following parameters are set for a table field:

* Field name in the class (structure)
* Field [data type](#data-types)
* Database table field name (default is the field name converted to snake_case)
* Name (brief description) of the field (default is empty)
* Detailed field description (default is empty)
* `NULL`/`NOT NULL` constraint (default is `NOT NULL` field)
* Default value (default is not set)
* Generated column parameters (default is not set)
* Field labels (default is not set)

The following syntax is used to create a field:

| Syntax                                   | Description                                                                                                     |
|------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| `"fieldName" :# datatype`                | Creates a field with name `fieldName` and type `datatype`                                                       |
| `"fieldName" :#? datatype`               | Creates a field with name `fieldName`, type `datatype` and `NULL` constraint                                    |
| `"fieldName" :#++ datatype`              | Creates a field with name `fieldName`, type `datatype` and `GENERATED ALWAYS AS IDENTITY` generation            |
| `dataElementTableField("dataElementId")` | Creates a field with parameters (name, type, description, etc.) from the specified data element `dataElementId` |
| `include("includeId")`                   | Creates a set of fields from the specified insertion template `includeId`                                       |

The data type `datatype` can be specified

* as a value indicated in the [Data Types](#data-types) section,
* as a string `"dataElementId"` in which case the field parameters are taken from the specified data element
  `dataElementId` (if they are not explicitly defined in the field description)

The following syntax is used to change field parameters

| Syntax              | Description                                                                                                |
|---------------------|------------------------------------------------------------------------------------------------------------|
| `:> "dbFieldName"`  | Sets the database table field name `dbFieldName`                                                           |
| `:@ "name"`         | Sets the name (brief description) of the field `name`                                                      |
| `:@@ "description"` | Sets the detailed field description `description`                                                          |
| `:*(/* labels */)`  | Sets field labels                                                                                          |
| `:= default-value`  | Sets the default value. The default value can be a string, number, or boolean value                        |
| `:== "statement"`   | Sets the default value as an expression `statement`. For example: `:== "CURRENT_TIMESTAMP"` - current time |
| `:& modificatior`   | Sets field parameters based on the modifier value `modificatior`. Valid modifier values are listed below.  |

Valid modifier values:

| Modifier                          | Description                                                                      |
|-----------------------------------|----------------------------------------------------------------------------------|
| `Null`                            | Sets the `NULL` constraint                                                       |
| `AutoInc`                         | Sets the generated column parameters `GENERATED ALWAYS AS IDENTITY`              |
| `GenAlwaysAsIdentity`             | Sets the generated column parameters `GENERATED ALWAYS AS IDENTITY`              |
| `GenByDefaultAsIdentity`          | Sets the generated column parameters `GENERATED BY DEFAULT AS IDENTITY`          |
| `GenStoredAlwaysAs("statement")`  | Sets the generated column parameters `GENERATED ALWAYS AS( $statement ) STORED`  |
| `GenVirtualAlwaysAs("statement")` | Sets the generated column parameters `GENERATED ALWAYS AS( $statement ) VIRTUAL` |

#### Relationships

Table relationships are specified in the `.withRelations` method. The following parameters are set for a relationship:

* Relationship identifier
* Name (brief description) of the relationship
* Detailed relationship description
* Relationship labels
* Relationship type: `OneToOne`, `ManyToOne`
* Identifier of the related table
* Related fields as a list of `"fieldOfTableA" -> "fieldOfTableB"`
* `onUpdate`/`onDelete` actions: `Cascade`, `Restrict`, `NoAction`, `SetNull`, `SetDefault`. Default is `NoAction`.
* Logical relationship flag - a logical relationship does not create a `FOREIGN KEY` constraint in the database
* Names of associated fields in entity structures

Relationships are described in the following form:

```scala
// real OneToOne relationship
oneToOne("id", "name", "relatedTableId", relatedFieldList)

// logical OneToOne relationship
logicalOneToOne("id", "name", "relatedTableId", relatedFieldList)

// real ManyToOne relationship
manyToOne("id", "name", "relatedTableId", relatedFieldList)

// logical ManyToOne relationship
logicalManyToOne("id", "name", "relatedTableId", relatedFieldList)
```

⚙️ **Arguments and Parameters**

| Parameter          | Description                                                                           | Mandatory |
|--------------------|---------------------------------------------------------------------------------------|-----------|
| `id`               | Unique relationship identifier                                                        | ✅         |
| `name`             | Name (brief description) of the relationship                                          | ✅         |
| `relatedTableId`   | Identifier of the related table                                                       | ✅         |
| `relatedFieldList` | Related fields as a list of `"fieldOfTableA" -> "fieldOfTableB"`, separated by commas | ✅         |

🧩 **Configuration Methods**

| Method                            | Description                                                                                                     | Optional |
|-----------------------------------|-----------------------------------------------------------------------------------------------------------------|----------|
| `.withDescription("description")` | Detailed relationship description                                                                               | ✅        |
| `.withLabels(/* labels */)`       | Relationship labels                                                                                             | ✅        |
| `.withOnUpdate(foreignKeyAction)` | `onUpdate` actions. Valid `foreignKeyAction` values: `Cascade`, `Restrict`, `NoAction`, `SetNull`, `SetDefault` | ✅        |
| `.withOnDelete(foreignKeyAction)` | `onDelete` actions. Valid `foreignKeyAction` values: `Cascade`, `Restrict`, `NoAction`, `SetNull`, `SetDefault` | ✅        |
| `.withAssociation(association)`   | Names of associated fields in entity structures                                                                 | ✅        |

The `withAssociation` method allows defining associative fields in entity structures.

For example, for a relationship defined in the `Country` table:

```scala
manyToOne("languageId", "Reference to language", "Language", "languageId" -> "id")
  .withAssociation("language")
```

the one-way association `language` means that a field `Language *LanguageEntity` referencing the `LanguageEntity` entity
will be added to the `CountryEntity` entity.

An association can be two-way. For example, for a relationship defined in the `BusinessPartner` table:

```scala
oneToOne("debtorId", "Reference to Debtor", "Debtor", "debtorId" -> "id")
  .withAssociation("debitor" -> "businessPartner")
```

the two-way association `"debitor"  -> "businessPartner"` means that a field `Debitor *DebtorEntity` referencing the
`DebtorEntity` entity will be added to the `BusinessPartnerEntity` entity, and a field
`BusinessPartner *BusinessPartner` referencing the `BusinessPartnerEntity` entity will be added to the `DebtorEntity`
entity.

#### Indexes

Table indexes are specified in the `.withIndexes` method. The following parameters are set for indexes:

* Index identifier
* Name (brief description) of the index
* Detailed index description
* Relationship labels
* Unique index `UNIQUE` flag
* Index fields
* Indexing method: `BTreeMethod`, `HashMethod`, `GistMethod`, `SpGistMethod`, `GinMethod`, `BrinMethod`
* Indexing condition `WHERE`
* List of fields to be included in the index as non-key columns

An index is described in the following form:

```scala
// non-unique index
index("id", "name", indexFields)
  .withDescription(description)
  .withLabels(/* index labels */)
  .withMethod(indexMethod)
  .where(predicate)
  .include(includeFields)

// unique index
uniqueIndex("id", "name", indexFields)
  .withDescription(description)
  .withLabels(/* index labels */)
  .withMethod(indexMethod)
  .where(predicate)
  .include(includeFields)
```

⚙️ **Arguments and Parameters**

| Parameter     | Description                                                                                         | Mandatory |
|---------------|-----------------------------------------------------------------------------------------------------|-----------|
| `id`          | Unique index identifier                                                                             | ✅         |
| `name`        | Name (brief description) of the index                                                               | ✅         |
| `indexFields` | List of index fields, separated by commas. Detailed description of the field list format see below. | ✅         |

🧩 **Configuration Methods**

| Method                            | Description                                                                                                         | Optional |
|-----------------------------------|---------------------------------------------------------------------------------------------------------------------|----------|
| `.withDescription("description")` | Detailed index description                                                                                          | ✅        |
| `.withLabels(/* labels */)`       | Index labels                                                                                                        | ✅        |
| `.withMethod(indexMethod)`        | Indexing method. Valid values: `BTreeMethod`, `HashMethod`, `GistMethod`, `SpGistMethod`, `GinMethod`, `BrinMethod` | ✅        |
| `.where(predicate)`               | Indexing condition `WHERE`. The `predicate` contains a logical expression. For example: `cor_tip IN ('I', 'U')`     | ✅        |
| `.include(includeFields)`         | Adds a list of fields to the index that will be included as non-key columns                                         | ✅        |

The list of index fields can be defined using the following syntax:

| Syntax                        | Description                                                                                           |
|-------------------------------|-------------------------------------------------------------------------------------------------------|
| `"fieldName"`                 | Index on field `fieldName`, sorted in ascending order.                                                |
| `asc("fieldName")`            | Index on field `fieldName`, sorted in ascending order.                                                |
| `desc("fieldName")`           | Index on field `fieldName`, sorted in descending order.                                               |
| `asc("fieldName").nullsFirst` | Index on field `fieldName`, sorted in ascending order and NULL values are placed first after sorting. |
| `desc("fieldName").nullsLast` | Index on field `fieldName`, sorted in descending order and NULL values are placed last after sorting. |

Example:

```scala
index("filter", "Index to filter order by orderDate, customer & status", desc("orderDate"), "customerId", "status")
  .where("status <> 'DRAFT")
  .include("id", "postingDate")
,
```

### Structures

Structures are used to describe DTO objects and data operations. They are described in the following format:

```scala
struct("id", "name", "struct name")
  .withDescription("description")
  .withLabels(/* table labels */)
  .likeTable("tableId", fieldUsage)
  .likeTablePK("tableId")
  .withFields(/* field definitions */)
```

⚙️ **Arguments and Parameters**

| Parameter     | Description                                           | Mandatory |
|---------------|-------------------------------------------------------|-----------|
| `id`          | Unique structure identifier. Converted to PascalCase. | ✅         |
| `name`        | Name (brief description) of the structure             | ✅         |
| `struct name` | Structure name. If not specified, taken from `id`.    | ❌         |
| `description` | Detailed description (optional, can be multi-line)    | ❌         |

🧩 **Configuration Methods**

| Method                              | Description                                                                                                            | Optional |
|-------------------------------------|------------------------------------------------------------------------------------------------------------------------|----------|
| `.withLabels`                       | Structure labels                                                                                                       | ✅        |
| `.likeTable("tableId", fieldUsage)` | Forms a structure including the fields of table `tableId`. The inclusion rules are defined in `fieldUsage` (see below) | ✅        |
| `.likeTablePK("tableId")`           | Forms a structure including the primary key fields of table `tableId`                                                  | ✅        |
| `.withFields`                       | Describes [structure fields](#structure-fields)                                                                        | ✅        |

The `likeTable` method allows creating a structure similar to the table `tableId` specified in the method. To determine
which fields from the table will be included in the structure, the `fieldUsage` rule is used:

| Rule                       | Description                                                                       |
|----------------------------|-----------------------------------------------------------------------------------|
| `IncludeAllFields`         | All table fields are included in the structure                                    |
| `IncludePKFields`          | All primary key fields of the table are included in the structure                 |
| `IncludeFields(fieldList)` | The table fields listed in `fieldList` are included in the structure              |
| `ExcludeFields(fieldList)` | All table fields except those listed in `fieldList` are included in the structure |

#### Structure Fields

Structure fields are specified in the `.withFields` method of the structure definition. The following parameters are set
for a structure field:

* Field name in the class (structure)
* Field [data type](#data-types)
* Name (brief description) of the field (default is empty)
* Detailed field description (default is empty)
* Field optionality (default is non-optional field)

The following syntax is used to create a field:

| Syntax                                    | Description                                                                                     |
|-------------------------------------------|-------------------------------------------------------------------------------------------------|
| `"fieldName" :# datatype`                 | Creates a mandatory field with name `fieldName` and type `datatype`                             |
| `"fieldName" :##? datatype`               | Creates an optional field with name `fieldName` and type `datatype`                             |
| `dataElementStructField("dataElementId")` | Creates a field with parameters (name, type, description, etc.) from the specified data element |
| `includeStructFields("includeId")`        | Creates a set of fields from the [insertion template](#insertion-templates) `includeId`         |

The following syntax is used to change field parameters

| Syntax              | Description                                           |
|---------------------|-------------------------------------------------------|
| `:@ "name"`         | Sets the name (brief description) of the field `name` |
| `:@@ "description"` | Sets the detailed field description `description`     |

The data type `datatype` can be specified

* as a value indicated in the [Data Types](#data-types) section,
* as a string `"dataElementId"` in which case the field parameters are taken from the specified data element
  `dataElementId` (if they are not explicitly defined in the field description)

For structures that are associated with tables (using the `likeTable` method), the data types `linkedStruct` and
`linkedStructArray` can be used.

The data type `linkedStruct(structId, [relation])` adds a new field to the structure referencing the structure
`structId`.
This structure must also be associated with a table. There must be a relationship (real or logical) between the two
associated tables that links these tables. If there is only one relationship between the tables, it can be omitted. If
there are several, specifying the relationship `relation` is mandatory. Two ways of specifying the relationship are
allowed:

* `OwnRelation(relationId)` - the relationship `relationId` is defined in the table associated with the own structure (
  i.e., the structure in which the field is defined)
* `OwnRelation(relationId)` - the relationship `relationId` is defined in the table associated with the structure
  `structId`)

The data type `linkedStructArray(structId, [relationId])` adds a new field to the structure that is an array referencing
the structure `structId`.
This structure must also be associated with a table. There must be a relationship (real or logical) between the two
associated tables that links these tables. If there is only one relationship between the tables, it can be omitted. If
there are several, specifying the relationship `relationId`, which is defined in the table associated with the own
structure (i.e., the structure in which the field is defined), is mandatory.

Using the data types `linkedStruct` and `linkedStructArray` allows establishing a connection between two structures that
are associated with two related tables. This information makes it possible to generate source code that will load data
from two tables into the specified structures.

## Model Building and Artifact Generation

To generate artifacts, preliminary model building is required, during which the consistency of the domain model is
checked: uniqueness of names, existence of relationships, correctness of types, and much more.

Building is performed with the following expression:

```scala
val build = DomainBuilder.build(OrderDomain.data)
```

where `OrderDomain.data` is the domain data model described in Annette Data Dictionary DSL.

Depending on the returned result, either errors are output (if the model contains them) or artifacts are generated:

```scala
  build match {
  case Left(messages) =>
    // output errors
    messages.foreach(println)
  case Right(result) =>
    // output warnings
    result.warnings.foreach(println)
    // the built data model can be used to generate artifacts
    val domain = result.domain
  // ...
}
```

### DDL Scripts for PostgreSQL

To generate PostgreSQL DDL scripts, use `PostgresDDLGenerator`:

```scala
import biz.lobachev.annette.data_dictionary.generators.Generator
import biz.lobachev.annette.data_dictionary.generators.postgres_ddl.PostgresDDLGenerator

Generator.generate(
  PostgresDDLGenerator(
    domain = domain, // Built model
    filename = "pg_ddl.sql", // Name of the generated script file. Optional. Default is postgres_ddl.sql
  ),
  s"examples/${domain.id}/ddl", // Path where the generated file will be located
)
```

### DDL Scripts for ClickHouse

To generate ClickHouse DDL scripts, use `ClickHouseDDLGenerator`:

```scala
import biz.lobachev.annette.data_dictionary.generators.Generator
import biz.lobachev.annette.data_dictionary.generators.click_house_ddl.ClickHouseDDLGenerator


Generator.generate(
  ClickHouseDDLGenerator(
    domain = domain, // Built model
    filename = "pg_ddl.sql", // Name of the generated script file. Optional. Default is click_house_ddl.sql
  ),
  s"examples/${domain.id}/ddl", // Path where the generated file will be located
)
```

### Domain Model Description in Excel Format

To generate the domain data model in Excel format, use `ClickHouseDDLGenerator`:

```scala
import biz.lobachev.annette.data_dictionary.generators.Generator
import biz.lobachev.annette.data_dictionary.generators.xls_domain.DomainExcelGenerator


Generator.generate(
  DomainExcelGenerator(
    target = GoTarget, // Target language whose data types will be reflected in the generated file. Valid values: GoTarget, KotlinTarget
    filename = "go-finance-domain", // File name, without the `.xlsx` extension
    translation = Map.empty, // Column header translations
    domains = domain1, domain2, // List of domain models to be described in the file
  ),
  s"examples/${domain.id}", // Path where the generated file will be located
)
```

### Domain Model Schema in DBML Format

To generate the domain model schema in DBML format, use `DbmlGenerator`:

```scala
import biz.lobachev.annette.data_dictionary.generators.Generator
import biz.lobachev.annette.data_dictionary.generators.dbml.DbmlGenerator


Generator.generate(
  DbmlGenerator(
    domain = domain, // Built model
    filename = "my-domain.dbml", // Name of the generated script file. Optional. Default is diagram.dbml
  ),
  s"examples/${domain.id}", // Path where the generated file will be located
)
```

The generated schema can be used:

* in the editor at [dbdiagram.io](https://dbdiagram.io/)
* in the [drawDB](https://github.com/drawdb-io/drawdb) editor

### Excel Data Templates for Database Loading

To generate Excel data templates for database loading, use `ExcelDataTemplateGenerator`:

```scala
import biz.lobachev.annette.data_dictionary.generators.Generator
import biz.lobachev.annette.data_dictionary.generators.xls_data.ExcelDataTemplateGenerator


Generator.generate(
  Generator.generate(
    ExcelDataTemplateGenerator(
      domain, // Built model
    ),
    s"examples/${domain.id}/xls_data", // Path where the generated files will be located
  )
```

### Kotlin Source Code

To generate Kotlin source code, use `KotlinGenerator`:

```scala
import biz.lobachev.annette.data_dictionary.generators.Generator
import biz.lobachev.annette.data_dictionary.generators.kotlin.KotlinGenerator

Generator.generate(
  Generator.generate(
    KotlinGenerator(
      domain, // Built model
    ),
    s"examples/${domain.id}/kotlin", // Path where the generated files will be located
  )
```

### Go Source Code

To generate Go source code, use `GoGenerator`.

```scala
import biz.lobachev.annette.data_dictionary.generators.Generator
import biz.lobachev.annette.data_dictionary.generators.golang.{GoGenerator, GoGeneratorOptions, Gorm, Sqlx}

Generator.generate(
  GoGenerator(
    domain, // Built model
    GoGeneratorOptions(options), // Generation options
  ),
  s"examples/${domain.id}/go", // Path where the generated files will be located
)
```

Generation options can take the following values:

* `Sqlx` - generation for the [sqlx](https://github.com/jmoiron/sqlx) library
* `Gorm(fieldNullable)` - generation for the [Gorm](https://gorm.io/) library. If `fieldNullable = true` (default), then
  pointers are used for generating `NULL` fields in entities, otherwise `sql.Null[T]` is used.