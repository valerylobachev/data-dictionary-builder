package biz.lobachev.annette.data_dictionary.dsl

import biz.lobachev.annette.data_dictionary.model.*
import biz.lobachev.annette.data_dictionary.utils.StringSyntax.*

object DSL {

  // Domains

  def domain(id: String, name: String, description: String = ""): DomainDef =
    DomainDef(
      id = id.trim,
      name = name.trim,
      description = description.trim,
    )

  // Components

  def component(id: String, name: String, description: String = ""): ComponentDef =
    ComponentDef(
      id = id.trim,
      name = name.trim,
      description = description.trim,
    )

  // Enums

  def nativeEnum(
    id: String,
    name: String,
    length: Int,
    description: String = "",
  ) =
    EnumDataDef(
      id = id.trim,
      name = name.trim,
      description = description.trim,
      enumType = NativeEnum,
      strict = true,
      length = length,
      elements = Seq.empty,
    )

  def stringEnum(
    id: String,
    name: String,
    length: Int,
    description: String = "",
  ) =
    EnumDataDef(
      id = id.trim,
      name = name.trim,
      description = description.trim,
      enumType = StringEnum,
      strict = false,
      length = length,
      elements = Seq.empty,
    )

  def strictStringEnum(
    id: String,
    name: String,
    length: Int,
    description: String = "",
  ) =
    EnumDataDef(
      id = id.trim,
      name = name.trim,
      description = description.trim,
      enumType = StringEnum,
      strict = true,
      length = length,
      elements = Seq.empty,
    )

  def intEnum(
    id: String,
    name: String,
    description: String = "",
  ) =
    EnumDataDef(
      id = id.trim,
      name = name.trim,
      description = description.trim,
      enumType = IntEnum,
      elements = Seq.empty,
    )

  def strictIntEnum(
    id: String,
    name: String,
    description: String = "",
  ) =
    EnumDataDef(
      id = id.trim,
      name = name.trim,
      description = description.trim,
      enumType = IntEnum,
      strict = true,
      elements = Seq.empty,
    )

  // Data elements

  def dataElement(
    id: String,
    fieldName: String,
    datatype: Datatype,
    name: String,
  ): DataElementDef =
    DataElementDef(
      id = id.trim.pascalCase,
      name = name.trim,
      fieldName = fieldName.trim.camelCase,
      dbFieldName = fieldName.trim.snakeCase,
      datatype = datatype,
    )

  def dataElementDb(
    id: String,
    fieldName: String,
    dbFieldName: String,
    datatype: Datatype,
    name: String,
  ): DataElementDef =
    DataElementDef(
      id = id.trim.pascalCase,
      name = name.trim,
      fieldName = fieldName.trim.camelCase,
      dbFieldName = dbFieldName.trim.snakeCase,
      datatype = datatype,
    )

  implicit class DataElementFieldImplicit(dataElementId: String) {
    def :!(fieldName: String) = dataElement(dataElementId, fieldName, integer, "")
  }

  implicit class DataElementImplicit(dataElement: DataElementDef) {
    def :#(dataElementId: String) = dataElement.copy(datatype = DataElementDatatype(dataElementId))
    def :#(datatype: Datatype)    = dataElement.copy(datatype = datatype)
    def :>(dbFieldName: String)   = dataElement.copy(dbFieldName = dbFieldName.trim.snakeCase)
    def :@(name: String)          = dataElement.copy(name = name.trim)
    def :@@(description: String)  = dataElement.copy(description = description.trim)
    def :*(seq: Label*)           = dataElement.copy(labels = dataElement.labels ++ seq)
  }

  // Includes

  def includeDef(id: String, name: String): IncludeDef =
    IncludeDef(
      id = id.trim.pascalCase,
      name = name.trim,
    )

  // Tables

  def table(id: String, name: String, entityName: String): TableDef =
    TableDef(
      id = id.trim.pascalCase,
      name = name.trim,
      entityName = entityName.trim.pascalCase,
      tableName = entityName.trim.pluralize.snakeCase,
    )

  def table(id: String, name: String): TableDef =
    TableDef(
      id = id.trim.pascalCase,
      name = name.trim,
      entityName = id.trim.pascalCase,
      tableName = id.trim.pluralize.snakeCase,
    )

  // Table fields

  def field(
    fieldName: String,
    datatype: Datatype,
    name: String,
    notNull: Boolean = true,
  ): TableFieldDefinition =
    TableFieldDefinition(
      name = name.trim,
      fieldName = fieldName.trim.camelCase,
      dbFieldName = fieldName.trim.snakeCase,
      datatype = datatype,
      notNull = notNull,
    )

  def fieldDb(
    fieldName: String,
    dbFieldName: String,
    datatype: Datatype,
    name: String,
    notNull: Boolean = true,
  ): TableFieldDefinition =
    TableFieldDefinition(
      name = name.trim,
      fieldName = fieldName.trim.camelCase,
      dbFieldName = dbFieldName.trim.snakeCase,
      datatype = datatype,
      notNull = notNull,
    )

  def dataElementTableField(dataElementId: String) = TableFieldDefinition(
    name = "",
    fieldName = "",
    dbFieldName = "",
    datatype = DataElementDatatype(dataElementId),
  )

  sealed trait TableFieldModificator

  case object Null                                 extends TableFieldModificator
  case object AutoInc                              extends TableFieldModificator
  case object GenAlwaysAsIdentity                  extends TableFieldModificator
  case object GenByDefaultAsIdentity               extends TableFieldModificator
  case class GenStoredAlwaysAs(statement: String)  extends TableFieldModificator
  case class GenVirtualAlwaysAs(statement: String) extends TableFieldModificator

  implicit class TableFieldImplicit(tableField: TableFieldDefinition) {
    def :&(modificator: TableFieldModificator) =
      modificator match {
        case Null                          => tableField.copy(notNull = false)
        case AutoInc                       => tableField.copy(generated = Some(GeneratedAlwaysAsIdentity))
        case GenAlwaysAsIdentity           => tableField.copy(generated = Some(GeneratedAlwaysAsIdentity))
        case GenByDefaultAsIdentity        => tableField.copy(generated = Some(GeneratedByDefaultAsIdentity))
        case GenStoredAlwaysAs(statement)  => tableField.copy(generated = Some(GeneratedAlwaysAs(statement, Stored)))
        case GenVirtualAlwaysAs(statement) => tableField.copy(generated = Some(GeneratedAlwaysAs(statement, Virtual)))
      }

    def :=(value: String)      = tableField.copy(default = Some(DefaultValue(value)))
    def :=(value: Int)         = tableField.copy(default = Some(DefaultValue(value.toString)))
    def :=(value: Boolean)     = tableField.copy(default = Some(DefaultValue(value.toString)))
    def :=(value: Double)      = tableField.copy(default = Some(DefaultValue(value.toString)))
    def :==(statement: String) = tableField.copy(default = Some(DefaultStatement(statement)))

    def :>(dbFieldName: String)  = tableField.copy(dbFieldName = dbFieldName.trim.snakeCase)
    def :@(name: String)         = tableField.copy(name = name.trim)
    def :@@(description: String) = tableField.copy(description = description.trim)
    def :*(seq: Label*)          = tableField.copy(labels = tableField.labels ++ seq)

  }

  implicit class DatatypeImplicit(fieldName: String) {
    def :#(dataElementId: String)   = field(fieldName.trim, DataElementDatatype(dataElementId.trim), "")
    def :#?(dataElementId: String)  = field(fieldName.trim, DataElementDatatype(dataElementId.trim), "", false)
    def :#++(dataElementId: String) = field(fieldName.trim, DataElementDatatype(dataElementId.trim), "", true)
      .copy(generated = Some(GeneratedAlwaysAsIdentity))
    def :#(datatype: Datatype)      = field(fieldName.trim, datatype, "")
    def :#?(datatype: Datatype)     = field(fieldName.trim, datatype, "", false)
    def :#++(datatype: Datatype)    =
      field(fieldName.trim, datatype, "", true).copy(generated = Some(GeneratedAlwaysAsIdentity))
  }

  def include(includeId: String) = IncludeTableFields(includeId)

  // Indexes

  def index(id: String, name: String, fields: IndexField*) =
    TableIndexDef(
      id = id.trim,
      name = name.trim,
      fields = fields,
    )

  def uniqueIndex(id: String, name: String, fields: IndexField*) =
    TableIndexDef(
      id = id.trim,
      name = name.trim,
      unique = true,
      fields = fields,
    )

  def asc(fieldName: String) = IndexField(fieldName)

  def desc(fieldName: String) = IndexField(fieldName, true)

  implicit class IndexFieldImplicit(fieldName: String) {}
  // Relations

  def manyToOne(
    id: String,
    name: String,
    referenceEntityId: String,
    fields: (String, String)*,
  ) =
    TableRelationDef(
      id = id.trim,
      name = name.trim,
      relationType = ManyToOne,
      referenceTableId = referenceEntityId.trim,
      onUpdate = NoAction,
      onDelete = NoAction,
      fields = fields,
    )

  def logicalManyToOne(
    id: String,
    name: String,
    referenceEntityId: String,
    fields: (String, String)*,
  ) =
    TableRelationDef(
      id = id.trim,
      name = name.trim,
      relationType = ManyToOne,
      referenceTableId = referenceEntityId.trim,
      onUpdate = NoAction,
      onDelete = NoAction,
      fields = fields,
      logical = true,
    )

  def oneToOne(
    id: String,
    name: String,
    referenceEntityId: String,
    fields: (String, String)*,
  ) =
    TableRelationDef(
      id = id.trim,
      name = name.trim,
      relationType = OneToOne,
      referenceTableId = referenceEntityId.trim,
      onUpdate = NoAction,
      onDelete = NoAction,
      fields = fields,
    )

  def logicalOneToOne(
    id: String,
    name: String,
    referenceEntityId: String,
    fields: (String, String)*,
  ) =
    TableRelationDef(
      id = id.trim,
      name = name.trim,
      relationType = OneToOne,
      referenceTableId = referenceEntityId.trim,
      onUpdate = NoAction,
      onDelete = NoAction,
      fields = fields,
      logical = true,
    )

  // Structs

  def struct(id: String, name: String, structName: String) =
    StructDef(
      id = id.trim.pascalCase,
      name = name.trim,
      structName = structName.trim.pascalCase,
    )

  def struct(id: String, name: String) =
    StructDef(
      id = id.trim.pascalCase,
      name = name.trim,
      structName = id.trim.pascalCase,
    )

  // Struct fields

  def structField(
    fieldName: String,
    datatype: Datatype,
    name: String,
    optional: Boolean = false,
  ) =
    StructFieldDefinition(
      fieldName = fieldName.trim.camelCase,
      name = name.trim,
      datatype = datatype,
      optional = optional,
    )

  def dataElementStructField(dataElementId: String) = StructFieldDefinition(
    name = "",
    fieldName = "",
    datatype = DataElementDatatype(dataElementId),
  )

  implicit class StructFieldImplicit(structField: StructFieldDefinition) {
    def :@(name: String) = structField.copy(name = name.trim)

    def :@@(description: String) = structField.copy(description = description.trim)
  }

  implicit class StructDatatypeImplicit(fieldName: String) {
    def :##(dataElementId: String) = structField(fieldName.trim, DataElementDatatype(dataElementId.trim), "")

    def :##?(dataElementId: String) = structField(fieldName.trim, DataElementDatatype(dataElementId.trim), "", true)

    def :##(datatype: Datatype) = structField(fieldName.trim, datatype, "")

    def :##?(datatype: Datatype) = structField(fieldName.trim, datatype, "", true)
  }

  def includeStructFields(includeId: String) = IncludeStructFields(includeId)

  def including(seq: String*) = IncludeFields(seq)

  def excluding(seq: String*) = ExcludeFields(seq)

  // Data types

  def varchar(length: Int)                = VarcharDatatype(length)
  def char(length: Int)                   = CharDatatype(length)
  def string                              = StringDatatype
  def json                                = JsonDatatype
  def jsonb                               = JsonBDatatype
  def goInt                               = GoIntDatatype
  def integer                             = IntegerDatatype
  def long                                = LongDatatype
  def smallint                            = SmallintDatatype
  def smallSerial                         = SmallSerialDatatype
  def serial                              = SerialDatatype
  def bigSerial                           = BigSerialDatatype
  def int8                                = Int8Datatype
  def uint                                = UIntDatatype
  def uint8                               = UInt8Datatype
  def uint16                              = UInt16Datatype
  def uint32                              = UInt32Datatype
  def uint64                              = UInt64Datatype
  def decimal(precision: Int, scale: Int) = DecimalDatatype(precision, scale)
  def double                              = DoubleDatatype
  def float                               = FloatDatatype
  def bool                                = BooleanDatatype
  def uuid                                = UuidDatatype
  def instant                             = InstantDatatype
  def offsetDateTime                      = OffsetDateTimeDatatype
  def dateTime                            = DateTimeDatatype
  def date                                = DateDatatype
  def time                                = TimeDatatype

  def enumeration(enumId: String)        = EnumDatatype(enumId)
  def dataElement(dataElementId: String) = DataElementDatatype(dataElementId)

  def array(datatype: Datatype)     = ArrayDatatype(datatype)
  def set(datatype: Datatype)       = SetDatatype(datatype)
  def stringMap(datatype: Datatype) = StringMapDatatype(datatype)

  def struct(structId: String)                                               = StructDatatype(structId)
  def linkedStruct(structId: String, relation: RelationRef = DeriveRelation) = StructDatatype(structId, Some(relation))
  def structArray(structId: String)                                          = StructArrayDatatype(structId)
  def linkedStructArray(structId: String, relationId: String = "")           = StructArrayDatatype(structId, Some(relationId))

}
