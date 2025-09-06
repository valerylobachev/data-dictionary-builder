package biz.lobachev.annette.data_dictionary.builder.model

sealed trait FieldDatatype

case object BoolField       extends FieldDatatype
case object StringField     extends FieldDatatype
case object IntField        extends FieldDatatype
case object Int8Field       extends FieldDatatype
case object Int16Field      extends FieldDatatype
case object Int32Field      extends FieldDatatype
case object Int64Field      extends FieldDatatype
case object UIntField       extends FieldDatatype
case object UInt8Field      extends FieldDatatype
case object UInt16Field     extends FieldDatatype
case object UInt32Field     extends FieldDatatype
case object UInt64Field     extends FieldDatatype
case object ByteField       extends FieldDatatype
case object Float32Field    extends FieldDatatype
case object Float64Field    extends FieldDatatype
case object Complex64Field  extends FieldDatatype
case object Complex128Field extends FieldDatatype

case class EnumField(enumId: String) extends FieldDatatype
case object UuidField                extends FieldDatatype
case object DecimalField             extends FieldDatatype
case object JsonField                extends FieldDatatype

case object InstantDateTimeField extends FieldDatatype
case object LocalDateTimeField   extends FieldDatatype
case object LocalDateField       extends FieldDatatype
case object LocalTimeField       extends FieldDatatype

case class DataElementField(dataElementId: String) extends FieldDatatype

case class ObjectField(modelId: String)        extends FieldDatatype
case class ObjectArrayField(modelId: String)   extends FieldDatatype
case class ArrayField(datatype: FieldDatatype) extends FieldDatatype

case class LinkedObjectField(modelId: String, relation: RelationLink)  extends FieldDatatype
case class LinkedObjectArrayField(modelId: String, relationId: String) extends FieldDatatype
//
//sealed trait RelationLink
//case class OwnRelationLink(relationId: String)        extends RelationLink
//case class AssociatedRelationLink(relationId: String) extends RelationLink
