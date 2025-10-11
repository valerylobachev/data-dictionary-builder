package biz.lobachev.annette.data_dictionary.model

import biz.lobachev.annette.data_dictionary.labels.{ClickHouse, Go, Java, Postgres}
import biz.lobachev.annette.data_dictionary.utils.StringSyntax.*

case class FullTableName(
  schema: Option[String],
  tableName: String,
) {
  def toDbName: String =
    schema
      .map(s => s"${wrapQuotes(s)}.${wrapQuotes(tableName)}")
      .getOrElse(wrapQuotes(tableName))
}

trait DomainMetaHelper {

  def getDataElementTargetDatatype(dataElement: DataElement, target: Target, domain: Domain): TargetDatatype =
    target match {
      case PostgresTarget => getPostgresDataElementDatatype(dataElement, domain)
      case GoTarget       => getGoDataElementDatatype(dataElement, domain)
      case KotlinTarget   => getKotlinDataElementDatatype(dataElement, domain)
      case _              => throw NotImplementedError("")
    }

  def getTableFieldTargetDatatype(field: TableField, target: Target, domain: Domain): TargetDatatype =
    target match {
      case PostgresTarget   => getPostgresTableFieldDatatype(field, domain)
      case ClickHouseTarget => getClickHouseTableFieldDatatype(field, domain)
      case GoTarget         => getGoTableFieldDatatype(field, domain)
      case KotlinTarget     => getKotlinTableFieldDatatype(field, domain)
      case _                => throw NotImplementedError("")
    }

  def getTargetDatatype(datatype: Datatype, target: Target, domain: Domain): TargetDatatype =
    target match {
      case PostgresTarget   => getPostgresDatatype(datatype, domain)
      case ClickHouseTarget => getClickHouseDatatype(datatype, domain)
      case GoTarget         => getGoDatatype(datatype, domain)
      case KotlinTarget     => getKotlinDatatype(datatype, domain)
      case _                => throw NotImplementedError("")
    }

  def getPostgresDataElementDatatype(
    dataElement: DataElement,
    domain: Domain,
  ): PostgresTargetDatatype =
    dataElement.labels
      .get(Postgres.POSTGRES_DATA_TYPE)
      .map(dt => PostgresTargetDatatype(dt))
      .getOrElse(
        getPostgresDatatype(dataElement.datatype, domain),
      )

  def getPostgresTableFieldDatatype(field: TableField, domain: Domain): PostgresTargetDatatype =
    field.labels
      .get(Postgres.POSTGRES_DATA_TYPE)
      .map(dt => PostgresTargetDatatype(dt))
      .getOrElse(
        getPostgresDatatype(field.datatype, domain),
      )

  def getPostgresDatatype(
    datatype: Datatype,
    domain: Domain,
  ): PostgresTargetDatatype =
    datatype match {
      case VarcharDatatype(length)                   => PostgresTargetDatatype(s"varchar($length)")
      case CharDatatype(length)                      => PostgresTargetDatatype(s"char($length)")
      case StringDatatype                            => PostgresTargetDatatype(s"text")
      case JsonDatatype                              => PostgresTargetDatatype(s"json")
      case JsonBDatatype                             => PostgresTargetDatatype(s"jsonb")
      case GoIntDatatype                             => PostgresTargetDatatype(s"bigint")
      case SmallintDatatype                          => PostgresTargetDatatype(s"smallint")
      case IntegerDatatype                           => PostgresTargetDatatype(s"integer")
      case LongDatatype                              => PostgresTargetDatatype(s"bigint")
      case SmallSerialDatatype                       => PostgresTargetDatatype(s"smallserial")
      case SerialDatatype                            => PostgresTargetDatatype(s"serial")
      case BigSerialDatatype                         => PostgresTargetDatatype(s"bigserial")
      case Int8Datatype                              => PostgresTargetDatatype(s"")
      case UIntDatatype                              => PostgresTargetDatatype(s"")
      case UInt8Datatype                             => PostgresTargetDatatype(s"")
      case UInt16Datatype                            => PostgresTargetDatatype(s"")
      case UInt32Datatype                            => PostgresTargetDatatype(s"")
      case UInt64Datatype                            => PostgresTargetDatatype(s"")
      case DecimalDatatype(precision, scale)         => PostgresTargetDatatype(s"decimal($precision,$scale)")
      case FloatDatatype                             => PostgresTargetDatatype(s"real")
      case DoubleDatatype                            => PostgresTargetDatatype(s"double precision")
      case BooleanDatatype                           => PostgresTargetDatatype(s"boolean")
      case UuidDatatype                              => PostgresTargetDatatype(s"uuid")
      case InstantDatatype                           => PostgresTargetDatatype(s"timestamptz")
      case OffsetDateTimeDatatype                    => PostgresTargetDatatype(s"timestamptz")
      case DateTimeDatatype                          => PostgresTargetDatatype(s"timestamp")
      case DateDatatype                              => PostgresTargetDatatype(s"date")
      case TimeDatatype                              => PostgresTargetDatatype(s"time")
      case EnumDatatype(enumId)                      =>
        val e = domain.enums(enumId)
        e.enumType match {
          case NativeEnum => PostgresTargetDatatype(enumId.snakeCase)
          case StringEnum => PostgresTargetDatatype(s"varchar(${e.length})")
          case IntEnum    => PostgresTargetDatatype("integer")
        }
      case DataElementDatatype(dataElementId)        =>
        val dataElement = domain.dataElements(dataElementId)
        getPostgresDataElementDatatype(dataElement, domain)
      case ArrayDatatype(datatype)                   => PostgresTargetDatatype(s"")
      case SetDatatype(datatype)                     => PostgresTargetDatatype(s"")
      case StringMapDatatype(datatype)               => PostgresTargetDatatype(s"")
      case StructDatatype(structId, relation)        => PostgresTargetDatatype(s"")
      case StructArrayDatatype(structId, relationId) => PostgresTargetDatatype(s"")
    }

  def getClickHouseDataElementDatatype(
    dataElement: DataElement,
    domain: Domain,
  ): ClickHouseTargetDatatype =
    dataElement.labels
      .get(ClickHouse.CLICK_HOUSE_DATA_TYPE)
      .map(dt => ClickHouseTargetDatatype(dt))
      .getOrElse(
        getClickHouseDatatype(dataElement.datatype, domain),
      )

  def getClickHouseTableFieldDatatype(field: TableField, domain: Domain): ClickHouseTargetDatatype =
    field.labels
      .get(ClickHouse.CLICK_HOUSE_DATA_TYPE)
      .map(dt => ClickHouseTargetDatatype(dt))
      .getOrElse(
        getClickHouseDatatype(field.datatype, domain),
      )

  def getClickHouseDatatype(
    datatype: Datatype,
    domain: Domain,
  ): ClickHouseTargetDatatype =
    datatype match {
      case VarcharDatatype(length)            => ClickHouseTargetDatatype(s"FixedString($length)")
      case CharDatatype(length)               => ClickHouseTargetDatatype(s"FixedString($length)")
      case StringDatatype                     => ClickHouseTargetDatatype(s"String")
      case JsonDatatype                       => ClickHouseTargetDatatype(s"JSON")
      case JsonBDatatype                      => ClickHouseTargetDatatype(s"JSON")
      case GoIntDatatype                      => ClickHouseTargetDatatype(s"Int64")
      case SmallintDatatype                   => ClickHouseTargetDatatype(s"Int16")
      case IntegerDatatype                    => ClickHouseTargetDatatype(s"Int32")
      case LongDatatype                       => ClickHouseTargetDatatype(s"Int64")
      case SmallSerialDatatype                => ClickHouseTargetDatatype(s"Int16")
      case SerialDatatype                     => ClickHouseTargetDatatype(s"Int32")
      case BigSerialDatatype                  => ClickHouseTargetDatatype(s"Int64")
      case Int8Datatype                       => ClickHouseTargetDatatype(s"Int8")
      case UIntDatatype                       => ClickHouseTargetDatatype(s"UInt64")
      case UInt8Datatype                      => ClickHouseTargetDatatype(s"UInt8")
      case UInt16Datatype                     => ClickHouseTargetDatatype(s"UInt16")
      case UInt32Datatype                     => ClickHouseTargetDatatype(s"UInt32")
      case UInt64Datatype                     => ClickHouseTargetDatatype(s"UInt64")
      case DecimalDatatype(precision, scale)  => ClickHouseTargetDatatype(s"Decimal($precision,$scale)")
      case FloatDatatype                      => ClickHouseTargetDatatype(s"Float32")
      case DoubleDatatype                     => ClickHouseTargetDatatype(s"Float64")
      case BooleanDatatype                    => ClickHouseTargetDatatype(s"Bool")
      case UuidDatatype                       => ClickHouseTargetDatatype(s"UUID")
      case InstantDatatype                    => ClickHouseTargetDatatype(s"DateTime64(9)")
      case OffsetDateTimeDatatype             => ClickHouseTargetDatatype(s"DateTime64(9)")
      case DateTimeDatatype                   => ClickHouseTargetDatatype(s"DateTime")
      case DateDatatype                       => ClickHouseTargetDatatype(s"Date")
      case TimeDatatype                       => ClickHouseTargetDatatype(s"Time")
      case EnumDatatype(enumId)               =>
        val e = domain.enums(enumId)
        e.enumType match {
          case NativeEnum => ClickHouseTargetDatatype(enumId.snakeCase)
          case StringEnum => ClickHouseTargetDatatype(s"FixedString(${e.length})")
          case IntEnum    => ClickHouseTargetDatatype("Int32")
        }
      case DataElementDatatype(dataElementId) =>
        val dataElement = domain.dataElements(dataElementId)
        getClickHouseDataElementDatatype(dataElement, domain)
      case ArrayDatatype(datatype)            => ClickHouseTargetDatatype(s"")
      case SetDatatype(datatype)              => ClickHouseTargetDatatype(s"")
      case StringMapDatatype(datatype)        => ClickHouseTargetDatatype(s"")

      case StructDatatype(structId, relation)        => ClickHouseTargetDatatype(s"")
      case StructArrayDatatype(structId, relationId) => ClickHouseTargetDatatype(s"")
    }

  def getGoDataElementDatatype(dataElement: DataElement, domain: Domain): GoTargetDatatype =
    dataElement.labels
      .get(Go.GO_DATA_TYPE)
      .map(dt => GoTargetDatatype(SimpleTypeVariant, None, dt))
      .getOrElse(
        getGoDatatype(dataElement.datatype, domain),
      )

  def getGoTableFieldDatatype(field: TableField, domain: Domain): GoTargetDatatype =
    field.labels
      .get(Go.GO_DATA_TYPE)
      .map(dt => GoTargetDatatype(SimpleTypeVariant, None, dt))
      .getOrElse(
        getGoDatatype(field.datatype, domain),
      )

  def getGoDatatype(datatype: Datatype, domain: Domain): GoTargetDatatype =
    datatype match {
      case VarcharDatatype(length)                   => GoTargetDatatype(SimpleTypeVariant, None, s"string")
      case CharDatatype(length)                      => GoTargetDatatype(SimpleTypeVariant, None, s"string")
      case StringDatatype                            => GoTargetDatatype(SimpleTypeVariant, None, s"string")
      case JsonDatatype                              => GoTargetDatatype(SimpleTypeVariant, None, s"string")
      case JsonBDatatype                             => GoTargetDatatype(SimpleTypeVariant, None, s"string")
      case GoIntDatatype                             => GoTargetDatatype(SimpleTypeVariant, None, s"int")
      case SmallintDatatype                          => GoTargetDatatype(SimpleTypeVariant, None, s"int16")
      case IntegerDatatype                           => GoTargetDatatype(SimpleTypeVariant, None, s"int32")
      case LongDatatype                              => GoTargetDatatype(SimpleTypeVariant, None, s"int64")
      case SmallSerialDatatype                       => GoTargetDatatype(SimpleTypeVariant, None, s"int16")
      case SerialDatatype                            => GoTargetDatatype(SimpleTypeVariant, None, s"int32")
      case BigSerialDatatype                         => GoTargetDatatype(SimpleTypeVariant, None, s"int64")
      case Int8Datatype                              => GoTargetDatatype(SimpleTypeVariant, None, s"int8")
      case UIntDatatype                              => GoTargetDatatype(SimpleTypeVariant, None, s"uint")
      case UInt8Datatype                             => GoTargetDatatype(SimpleTypeVariant, None, s"uint7")
      case UInt16Datatype                            => GoTargetDatatype(SimpleTypeVariant, None, s"uint16")
      case UInt32Datatype                            => GoTargetDatatype(SimpleTypeVariant, None, s"uint32")
      case UInt64Datatype                            => GoTargetDatatype(SimpleTypeVariant, None, s"uint64")
      case DecimalDatatype(precision, scale)         =>
        GoTargetDatatype(SimpleTypeVariant, Some(GoPackage("github.com/shopspring/decimal")), s"Decimal")
      case FloatDatatype                             => GoTargetDatatype(SimpleTypeVariant, None, s"float32")
      case DoubleDatatype                            => GoTargetDatatype(SimpleTypeVariant, None, s"float64")
      case BooleanDatatype                           => GoTargetDatatype(SimpleTypeVariant, None, s"bool")
      case UuidDatatype                              => GoTargetDatatype(SimpleTypeVariant, None, s"string")
      case InstantDatatype                           => GoTargetDatatype(SimpleTypeVariant, Some(GoPackage("time")), s"Time")
      case OffsetDateTimeDatatype                    => GoTargetDatatype(SimpleTypeVariant, Some(GoPackage("time")), s"Time")
      case DateTimeDatatype                          =>
        GoTargetDatatype(SimpleTypeVariant, Some(GoPackage("cloud.google.com/go/civil")), s"DateTime")
      case DateDatatype                              => GoTargetDatatype(SimpleTypeVariant, Some(GoPackage("cloud.google.com/go/civil")), s"Date")
      case TimeDatatype                              => GoTargetDatatype(SimpleTypeVariant, Some(GoPackage("cloud.google.com/go/civil")), s"Time")
      case EnumDatatype(enumId)                      =>
        val e = domain.enums(enumId)
        e.enumType match {
          case NativeEnum => GoTargetDatatype(SimpleTypeVariant, None, "string")
          case StringEnum => GoTargetDatatype(SimpleTypeVariant, None, "string")
          case IntEnum    => GoTargetDatatype(SimpleTypeVariant, None, "int32")
        }
      case DataElementDatatype(dataElementId)        =>
        val dataElement = domain.dataElements(dataElementId)
        getGoDataElementDatatype(dataElement, domain)
      case ArrayDatatype(datatype)                   =>
        val dt = getGoDatatype(datatype, domain)
        GoTargetDatatype(ArrayTypeVariant, dt.pkg, dt.datatype)
      case SetDatatype(datatype)                     =>
        val dt = getGoDatatype(datatype, domain)
        GoTargetDatatype(
          GenericTypeVariant(GoPackage("github.com/deckarep/golang-set", Some("v2")), "Set"),
          dt.pkg,
          dt.datatype,
        )
      case StringMapDatatype(datatype)               =>
        val dt = getGoDatatype(datatype, domain)
        GoTargetDatatype(MapTypeVariant, dt.pkg, dt.datatype)
      case StructDatatype(structId, relation)        =>
        val pkg    = getStructPackage(structId, GoTarget, domain).map(p => GoPackage(p))
        val struct = domain.structs(structId)
        GoTargetDatatype(SimpleTypeVariant, pkg, struct.structName)
      case StructArrayDatatype(structId, relationId) =>
        val pkg    = getStructPackage(structId, GoTarget, domain).map(p => GoPackage(p))
        val struct = domain.structs(structId)
        GoTargetDatatype(ArrayTypeVariant, pkg, struct.structName)
    }

  def getKotlinDataElementDatatype(dataElement: DataElement, domain: Domain): KotlinTargetDatatype =
    dataElement.labels
      .get(Java.KOTLIN_DATA_TYPE)
      .map(dt => KotlinTargetDatatype(None, dt))
      .getOrElse(
        getKotlinDatatype(dataElement.datatype, domain),
      )

  def getKotlinTableFieldDatatype(field: TableField, domain: Domain): KotlinTargetDatatype =
    field.labels
      .get(Java.KOTLIN_DATA_TYPE)
      .map(dt => KotlinTargetDatatype(None, dt))
      .getOrElse(
        getKotlinDatatype(field.datatype, domain),
      )

  def getKotlinDatatype(datatype: Datatype, domain: Domain): KotlinTargetDatatype =
    datatype match {
      case VarcharDatatype(length)                   => KotlinTargetDatatype(None, s"String")
      case CharDatatype(length)                      => KotlinTargetDatatype(None, s"String")
      case StringDatatype                            => KotlinTargetDatatype(None, s"String")
      case JsonDatatype                              => KotlinTargetDatatype(None, s"String")
      case JsonBDatatype                             => KotlinTargetDatatype(None, s"String")
      case GoIntDatatype                             => KotlinTargetDatatype(None, s"Long")
      case SmallintDatatype                          => KotlinTargetDatatype(None, s"Short")
      case IntegerDatatype                           => KotlinTargetDatatype(None, s"Int")
      case LongDatatype                              => KotlinTargetDatatype(None, s"Long")
      case SmallSerialDatatype                       => KotlinTargetDatatype(None, s"Short")
      case SerialDatatype                            => KotlinTargetDatatype(None, s"Int")
      case BigSerialDatatype                         => KotlinTargetDatatype(None, s"Long")
      case Int8Datatype                              => KotlinTargetDatatype(None, s"Byte")
      case UIntDatatype                              => KotlinTargetDatatype(None, s"")
      case UInt8Datatype                             => KotlinTargetDatatype(None, s"")
      case UInt16Datatype                            => KotlinTargetDatatype(None, s"")
      case UInt32Datatype                            => KotlinTargetDatatype(None, s"")
      case UInt64Datatype                            => KotlinTargetDatatype(None, s"")
      case DecimalDatatype(precision, scale)         => KotlinTargetDatatype(Some("java.math"), s"BigDecimal")
      case FloatDatatype                             => KotlinTargetDatatype(None, s"Float")
      case DoubleDatatype                            => KotlinTargetDatatype(None, s"Double")
      case BooleanDatatype                           => KotlinTargetDatatype(None, s"Boolean")
      case UuidDatatype                              => KotlinTargetDatatype(None, s"UUID")
      case InstantDatatype                           => KotlinTargetDatatype(Some("java.time"), s"Instant")
      case OffsetDateTimeDatatype                    => KotlinTargetDatatype(Some("java.time"), s"OffsetDateTime")
      case DateTimeDatatype                          => KotlinTargetDatatype(Some("java.time"), s"LocalDateTime")
      case DateDatatype                              => KotlinTargetDatatype(Some("java.time"), s"LocalDate")
      case TimeDatatype                              => KotlinTargetDatatype(Some("java.time"), s"LocalTime")
      case EnumDatatype(enumId)                      =>
        val e = domain.enums(enumId)
        e.enumType match {
          case NativeEnum => KotlinTargetDatatype(None, "String")
          case StringEnum => KotlinTargetDatatype(None, "String")
          case IntEnum    => KotlinTargetDatatype(None, "Int")
        }
      case DataElementDatatype(dataElementId)        =>
        val dataElement = domain.dataElements(dataElementId)
        getKotlinDataElementDatatype(dataElement, domain)
      case ArrayDatatype(datatype)                   =>
        val dt = getKotlinDatatype(datatype, domain)
        KotlinTargetDatatype(dt.pkg, dt.datatype, Some(KotlinCollectionDatatype(None, "List")))
      case SetDatatype(datatype)                     =>
        val dt = getKotlinDatatype(datatype, domain).datatype
        KotlinTargetDatatype(None, dt, Some(KotlinCollectionDatatype(None, "Set")))
      case StringMapDatatype(datatype)               =>
        val dt = getKotlinDatatype(datatype, domain).datatype
        KotlinTargetDatatype(None, dt, Some(KotlinCollectionDatatype(None, "Map")))
      case StructDatatype(structId, relation)        =>
        val pkg    = getStructPackage(structId, KotlinTarget, domain)
        val struct = domain.structs(structId)
        KotlinTargetDatatype(pkg, struct.structName)
      case StructArrayDatatype(structId, relationId) =>
        val pkg    = getStructPackage(structId, KotlinTarget, domain)
        val struct = domain.structs(structId)
        KotlinTargetDatatype(pkg, struct.structName, Some(KotlinCollectionDatatype(None, "List")))
    }

  def getStructPackage(structId: String, target: Target, domain: Domain) = {
    def labelId = target match {
      case ScalaTarget  => Java.JAVA_STRUCT_PACKAGE
      case GoTarget     => Go.GO_STRUCT_PACKAGE
      case KotlinTarget => Java.JAVA_STRUCT_PACKAGE
      case _            => "<not-exist>"
    }
    def struct  = domain.structs(structId)
    getStructLabel(struct, labelId, domain)
  }

  def getGoTablePackage(table: Table, domain: Domain) =
    getTableLabel(table, Go.GO_TABLE_PACKAGE, domain).getOrElse("model")

  def getGoEnumPackage(en: EnumData, domain: Domain) =
    getEnumLabel(en, Go.GO_ENUM_PACKAGE, domain).getOrElse("enums")

  def getGoStructPackage(data: Struct, domain: Domain) =
    getStructLabel(data, Go.GO_STRUCT_PACKAGE, domain).getOrElse("data_model")

  def getFullTableName(table: Table, domain: Domain) =
    FullTableName(
      getTableLabel(table, Postgres.POSTGRES_SCHEMA, domain),
      table.tableName,
    )

  def getEnumData(datatype: Datatype, domain: Domain): Option[EnumData] =
    datatype match {
      case EnumDatatype(enumId)               =>
        domain.enums.get(enumId)
      case DataElementDatatype(dataElementId) =>
        getEnumData(domain.dataElements(dataElementId).datatype, domain)
      case _                                  =>
        None
    }

  def getEnumLabel(en: EnumData, labelId: String, domain: Domain): Option[String] =
    en.labels
      .get(labelId)
      .orElse(
        getComponentLabel(en.componentId, labelId, domain),
      )

  def getTableLabel(table: Table, labelId: String, domain: Domain): Option[String] =
    table.labels
      .get(labelId)
      .orElse(
        getComponentLabel(table.componentId, labelId, domain),
      )

  def getStructLabel(struct: Struct, labelId: String, domain: Domain): Option[String] =
    struct.labels
      .get(labelId)
      .orElse(
        getComponentLabel(struct.componentId, labelId, domain),
      )

  def getComponentLabel(componentId: Option[String], labelId: String, domain: Domain): Option[String] =
    componentId match {
      case Some(compId) =>
        domain.components
          .get(compId)
          .flatMap(c =>
            c.labels
              .get(labelId)
              .orElse(getComponentLabel(c.parent, labelId, domain)),
          )
      case None         => getDomainLabel(domain, labelId)
    }

  def getDomainLabel(domain: Domain, labelId: String): Option[String] =
    domain.labels.get(labelId)

  def getTableField(tableFields: Seq[TableField], fieldName: String): TableField =
    tableFields
      .find(f => f.fieldName == fieldName)
      .getOrElse {
        throw new IllegalArgumentException(s"not found field $fieldName")
      }

  def getTableDbFieldName(tableFields: Seq[TableField], fieldName: String): String =
    tableFields
      .find(f => f.fieldName == fieldName)
      .map(f => wrapQuotes(f.dbFieldName))
      .getOrElse {
        throw new IllegalArgumentException(s"not found field $fieldName")
      }

  def getTablePrimaryKeyConstraintName(tableName: String): String               = tableName + "_pkey"
  
  def getTableForeignKeyConstraintName(tableName: String, fkId: String): String =
    s"${tableName}_fk_${fkId.snakeCase}"

  def getTableForeignKeyUniqueIndexConstraintName(tableName: String, fkId: String): String =
    s"${tableName}_fk_uq_${fkId.snakeCase}"

  def getTableIndexConstraintName(tableName: String, fkId: String): String =
    s"${tableName}_idx_${fkId.snakeCase}"
}
