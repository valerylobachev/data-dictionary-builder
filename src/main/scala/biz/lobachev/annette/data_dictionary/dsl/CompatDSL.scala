package biz.lobachev.annette.data_dictionary.dsl

import biz.lobachev.annette.data_dictionary.model.{
  BooleanDatatype,
  CharDatatype,
  DataElementDatatype,
  DateDatatype,
  DateTimeDatatype,
  DecimalDatatype,
  DoubleDatatype,
  EnumDatatype,
  FloatDatatype,
  InstantDatatype,
  IntegerDatatype,
  JsonBDatatype,
  JsonDatatype,
  LongDatatype,
  OffsetDateTimeDatatype,
  SmallintDatatype,
  StringDatatype,
  TimeDatatype,
  UuidDatatype,
  VarcharDatatype,
}
import biz.lobachev.annette.data_dictionary.labels.Postgres

import java.time.{Instant, LocalDate, LocalDateTime, LocalTime, OffsetDateTime}
import java.util.UUID

object CompatDSL {
//  @deprecated("use .includeDef", "0.5.0")
  def embeddedEntity(id: String, name: String): IncludeDef = DSL.includeDef(id, name)

  extension (c: ComponentDef)
//    @deprecated("use .withLabels(PostgresSchema.postgresSchema(schema))", "0.5.0")
    def withSchema(schema: String) = c.withLabels(Postgres.postgresSchema(schema))

  extension (c: ComponentDef)
//    @deprecated("use .withTables(...)", "0.5.0")
    def withEntities(seq: TableDef*) = c.withTables(seq*)

//  @deprecated("use .table", "0.5.0")
  def tableEntity(id: String, name: String, entityName: String): TableDef = DSL.table(id, name, entityName)

//  @deprecated("use .table", "0.5.0")
  def tableEntity(id: String, name: String): TableDef = DSL.table(id, name)

  def StringVarchar(length: Int, defaultValue: Option[String] = None)                        = VarcharDatatype(length)
  def StringChar(length: Int, defaultValue: Option[String] = None)                           = CharDatatype(length)
  def StringText(defaultValue: Option[String] = None)                                        = StringDatatype
  def StringJson(defaultValue: Option[String] = None)                                        = JsonDatatype
  def StringJsonB(defaultValue: Option[String] = None)                                       = JsonBDatatype
  def IntInt(defaultValue: Option[Int] = None)                                               = IntegerDatatype
  def LongLong(defaultValue: Option[Long] = None)                                            = LongDatatype
  def ShortSmallint(defaultValue: Option[Short] = None)                                      = SmallintDatatype
  def BigDecimalNumeric(precision: Int, scale: Int, defaultValue: Option[BigDecimal] = None) =
    DecimalDatatype(precision, scale)
  def BigIntegerNumeric(precision: Int, defaultValue: Option[BigDecimal] = None)             = DecimalDatatype(precision, 0)
  def DoubleDouble(defaultValue: Option[Double] = None)                                      = DoubleDatatype
  def FloatFloat(defaultValue: Option[Float] = None)                                         = FloatDatatype
  def BooleanBoolean(defaultValue: Option[Boolean] = None)                                   = BooleanDatatype
  def UuidUuid(defaultValue: Option[UUID] = None)                                            = UuidDatatype
  def InstantTimestamp(defaultValue: Option[Instant] = None)                                 = InstantDatatype
  def OffsetDateTimeTimestamp(defaultValue: Option[OffsetDateTime] = None)                   = OffsetDateTimeDatatype
  def LocalDateTimeTimestamp(defaultValue: Option[LocalDateTime] = None)                     = DateTimeDatatype
  def LocalDateDate(defaultValue: Option[LocalDate] = None)                                  = DateDatatype
  def LocalTimeTime(defaultValue: Option[LocalTime] = None)                                  = TimeDatatype

  def Enum(enumId: String, defaultValue: Option[String] = None) = EnumDatatype(enumId)
  def DataElementType(dataElementId: String)                    = DataElementDatatype(dataElementId)

}
