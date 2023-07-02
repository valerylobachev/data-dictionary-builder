package biz.lobachev.annette.data_dictionary.builder.model

import java.math.BigInteger
import java.time.{Instant, LocalDate, LocalDateTime, LocalTime, OffsetDateTime}
import java.util.UUID

sealed trait DataType

case class StringVarchar(lenght: Int, defaultValue: Option[String] = None) extends DataType

case class StringChar(lenght: Int, defaultValue: Option[String] = None) extends DataType
case class StringText(defaultValue: Option[String] = None)              extends DataType
case class StringJson(defaultValue: Option[String] = None)              extends DataType
case class StringJsonB(defaultValue: Option[String] = None)             extends DataType

case class IntInt(defaultValue: Option[Int] = None)        extends DataType
case class LongLong(defaultValue: Option[Long] = None)     extends DataType
case class ShortSmallint(defaultValue: Option[Int] = None) extends DataType

case class BigDecimalNumeric(precision: Int, scale: Int, defaultValue: Option[BigDecimal] = None) extends DataType
case class BigIntegerNumeric(precision: Int, defaultValue: Option[BigInteger] = None)             extends DataType

case class DoubleDouble(defaultValue: Option[Double] = None)  extends DataType
case class FloatFloat(defaultValue: Option[Float] = None)     extends DataType
case class BooleanBoolean(defaultValue: Option[Float] = None) extends DataType

case class UuidUuid(defaultValue: Option[UUID] = None) extends DataType

case class InstantTimestamp(defaultValue: Option[Instant] = None)               extends DataType
case class OffsetDateTimeTimestamp(defaultValue: Option[OffsetDateTime] = None) extends DataType

case class LocalDateTimeTimestamp(defaultValue: Option[LocalDateTime] = None) extends DataType
case class LocalDateDate(defaultValue: Option[LocalDate] = None)              extends DataType
case class LocalTimeTime(defaultValue: Option[LocalTime] = None)              extends DataType

case class EnumString(enumId: String, defaultValue: Option[String] = None) extends DataType

case class EmbeddedEntityType(entityId: String, withPrefix: Boolean = false, withRelations: Boolean = false)
    extends DataType
case class ObjectType(entityId: String)           extends DataType
case class DataElementType(dataElementId: String) extends DataType

case class ListCollection(dataType: DataType)      extends DataType
case class SetCollection(dataType: DataType)       extends DataType
case class StringMapCollection(dataType: DataType) extends DataType
