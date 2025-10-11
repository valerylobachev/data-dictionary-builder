package biz.lobachev.annette.data_dictionary.model

sealed trait Datatype

case class VarcharDatatype(length: Int)                extends Datatype
case class CharDatatype(length: Int)                   extends Datatype
case object StringDatatype                             extends Datatype
case object JsonDatatype                               extends Datatype
case object JsonBDatatype                              extends Datatype
case object GoIntDatatype                              extends Datatype
case object SmallintDatatype                           extends Datatype
case object IntegerDatatype                            extends Datatype
case object LongDatatype                               extends Datatype
case object SmallSerialDatatype                        extends Datatype
case object SerialDatatype                             extends Datatype
case object BigSerialDatatype                          extends Datatype
case object Int8Datatype                               extends Datatype
case object UIntDatatype                               extends Datatype
case object UInt8Datatype                              extends Datatype
case object UInt16Datatype                             extends Datatype
case object UInt32Datatype                             extends Datatype
case object UInt64Datatype                             extends Datatype
case class DecimalDatatype(precision: Int, scale: Int) extends Datatype
case object FloatDatatype                              extends Datatype
case object DoubleDatatype                             extends Datatype
case object BooleanDatatype                            extends Datatype
case object UuidDatatype                               extends Datatype
case object InstantDatatype                            extends Datatype
case object OffsetDateTimeDatatype                     extends Datatype
case object DateTimeDatatype                           extends Datatype
case object DateDatatype                               extends Datatype
case object TimeDatatype                               extends Datatype

case class EnumDatatype(enumId: String)               extends Datatype
case class DataElementDatatype(dataElementId: String) extends Datatype

case class ArrayDatatype(datatype: Datatype)     extends Datatype
case class SetDatatype(datatype: Datatype)       extends Datatype
case class StringMapDatatype(datatype: Datatype) extends Datatype

case class StructDatatype(structId: String, relation: Option[RelationRef] = None)   extends Datatype
case class StructArrayDatatype(structId: String, relationId: Option[String] = None) extends Datatype

sealed trait RelationRef
case object DeriveRelation                        extends RelationRef
case class OwnRelation(relationId: String)        extends RelationRef
case class AssociatedRelation(relationId: String) extends RelationRef
