package biz.lobachev.annette.data_dictionary.builder.model

import biz.lobachev.annette.data_dictionary.builder.builder.DomainBuilder
import biz.lobachev.annette.data_dictionary.builder.{POSTGRESQL, SCALA}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._
import biz.lobachev.annette.data_dictionary.builder.utils.Utils

import scala.collection.immutable.ListMap

case class Domain(
  id: String,
  name: String,
  description: String = "",
  rootComponentIds: Seq[String] = Seq.empty,
  components: ListMap[String, Component] = ListMap.empty,
  entities: ListMap[String, Entity] = ListMap.empty,
  dataElements: ListMap[String, DataElement] = ListMap.empty,
  enums: ListMap[String, EnumData] = ListMap.empty,
  labels: Labels = Map.empty,
  errors: Seq[String] = Seq.empty,
) {

  def withComponentSeq(componentSeq: Seq[ComponentData]): Domain = {
    val newRootComponentIds   = componentSeq.map(_.component.id)
    val newComponents         = componentSeq.flatMap(_.expandComponents())
    val newEntities           = componentSeq.flatMap(_.expandEntities())
    val newDataElements       = componentSeq.flatMap(_.expandDataElements())
    val newEnums              = componentSeq.flatMap(_.expandEnums())
    val componentDuplicates   = Utils.findDuplicates(components.keys.toSeq ++ newComponents.map(_.id))
    val entityDuplicates      = Utils.findDuplicates(entities.keys.toSeq ++ newEntities.map(_.id))
    val dataElementDuplicates = Utils.findDuplicates(dataElements.keys.toSeq ++ newDataElements.map(_.id))
    val enumDuplicates        = Utils.findDuplicates(enums.keys.toSeq ++ newEnums.map(_.id))
    val newErrors             = Seq(
      if (componentDuplicates.nonEmpty) Some("Duplicated components: " + componentDuplicates.mkString(", "))
      else None,
      if (entityDuplicates.nonEmpty) Some("Duplicated entities: " + entityDuplicates.mkString(", "))
      else None,
      if (dataElementDuplicates.nonEmpty) Some("Duplicated data elements: " + dataElementDuplicates.mkString(", "))
      else None,
      if (enumDuplicates.nonEmpty) Some("Duplicated enums: " + enumDuplicates.mkString(", "))
      else None,
    ).flatten

    copy(
      rootComponentIds = rootComponentIds ++ newRootComponentIds,
      components = components ++ newComponents.map(c => c.id -> c),
      entities = entities ++ newEntities.map(e => e.id -> e),
      dataElements = dataElements ++ newDataElements.map(e => e.id -> e),
      errors = errors ++ newErrors,
    )
  }

  def withComponents(componentSeq: ComponentData*) = withComponentSeq(componentSeq)

  @deprecated("use withComponents", since = "0.4.0")
  def withGroups(componentSeq: ComponentData*) = withComponentSeq(componentSeq)

  def withDataElementSeq(seq: Seq[DataElement]) = {
    val dataElementDuplicates = Utils.findDuplicates(dataElements.keys.toSeq ++ seq.map(_.id))
    val newErrors             =
      if (dataElementDuplicates.nonEmpty) Seq("Duplicated data elements: " + dataElementDuplicates.mkString(", "))
      else Seq.empty
    if (dataElementDuplicates.nonEmpty) throw new IllegalArgumentException("Duplicated items")
    copy(
      dataElements = dataElements ++ ListMap.from(seq.map(e => e.id -> e)),
      errors = errors ++ newErrors,
    )
  }

  def withDataElements(seq: DataElement*) = withDataElementSeq(seq)

  def withEnumSeq(seq: Seq[EnumData]) =
    copy(enums = ListMap.from(seq.map(e => e.id -> e)))

  def withEnums(seq: EnumData*) = withEnumSeq(seq)

  def withLabels(seq: Label*) = copy(labels = labels ++ seq.map(a => a.key -> a.value))

  def build(): Either[Seq[String], Domain] =
    DomainBuilder.build(this)

  def getTargetDataType(dataType: DataType, target: String): String =
    dataType match {
      case StringVarchar(lenght, _)               =>
        target match {
          case SCALA      => "String"
          case POSTGRESQL => s"varchar($lenght)"
          case _          => UNDEFINED_DATA_TYPE
        }
      case StringChar(lenght, _)                  =>
        target match {
          case SCALA      => "String"
          case POSTGRESQL => s"char($lenght)"
          case _          => UNDEFINED_DATA_TYPE
        }
      case StringText(_)                          =>
        target match {
          case SCALA      => "String"
          case POSTGRESQL => "text"
          case _          => UNDEFINED_DATA_TYPE
        }
      case StringJson(_)                          =>
        target match {
          case SCALA      => "String"
          case POSTGRESQL => "json"
          case _          => UNDEFINED_DATA_TYPE
        }
      case StringJsonB(_)                         =>
        target match {
          case SCALA      => "String"
          case POSTGRESQL => "jsonb"
          case _          => UNDEFINED_DATA_TYPE
        }
      case IntInt(_)                              =>
        target match {
          case SCALA      => "Int"
          case POSTGRESQL => s"integer"
          case _          => UNDEFINED_DATA_TYPE
        }
      case LongLong(_)                            =>
        target match {
          case SCALA      => "Long"
          case POSTGRESQL => s"bigint"
          case _          => UNDEFINED_DATA_TYPE
        }
      case ShortSmallint(_)                       =>
        target match {
          case SCALA      => "Short"
          case POSTGRESQL => "smallint"
          case _          => UNDEFINED_DATA_TYPE
        }
      case BigDecimalNumeric(precision, scale, _) =>
        target match {
          case SCALA      => "BigDecimal"
          case POSTGRESQL => s"decimal($precision,$scale)"
          case _          => UNDEFINED_DATA_TYPE
        }
      case BigIntegerNumeric(precision, _)        =>
        target match {
          case SCALA      => "BigInteger"
          case POSTGRESQL => s"decimal($precision,0)"
          case _          => UNDEFINED_DATA_TYPE
        }
      case DoubleDouble(_)                        =>
        target match {
          case SCALA      => "Double"
          case POSTGRESQL => "float8"
          case _          => UNDEFINED_DATA_TYPE
        }
      case FloatFloat(_)                          =>
        target match {
          case SCALA      => "Float"
          case POSTGRESQL => "float4"
          case _          => UNDEFINED_DATA_TYPE
        }
      case BooleanBoolean(_)                      =>
        target match {
          case SCALA      => "Boolean"
          case POSTGRESQL => "boolean"
          case _          => UNDEFINED_DATA_TYPE
        }
      case UuidUuid(_)                            =>
        target match {
          case SCALA      => "UUID"
          case POSTGRESQL => "uuid"
          case _          => UNDEFINED_DATA_TYPE
        }
      case InstantTimestamp(_)                    =>
        target match {
          case SCALA      => "Instant"
          case POSTGRESQL => "timestamptz"
          case _          => UNDEFINED_DATA_TYPE
        }
      case OffsetDateTimeTimestamp(_)             =>
        target match {
          case SCALA      => "OffsetDateTime"
          case POSTGRESQL => "timestamptz"
          case _          => UNDEFINED_DATA_TYPE
        }
      case LocalDateTimeTimestamp(_)              =>
        target match {
          case SCALA      => "LocalDateTime"
          case POSTGRESQL => "timestamp"
          case _          => UNDEFINED_DATA_TYPE
        }
      case LocalDateDate(_)                       =>
        target match {
          case SCALA      => "LocalDate"
          case POSTGRESQL => "date"
          case _          => UNDEFINED_DATA_TYPE
        }
      case LocalTimeTime(_)                       =>
        target match {
          case SCALA      => "LocalTime"
          case POSTGRESQL => "time"
          case _          => UNDEFINED_DATA_TYPE
        }
      case EnumString(enumId, _)                  =>
        target match {
          case SCALA      => enumId.pascalCase
          case POSTGRESQL => enumId.snakeCase
          case _          => UNDEFINED_DATA_TYPE
        }
      case EmbeddedEntityType(_, _, _)            =>
        UNDEFINED_DATA_TYPE
      case ObjectType(entityId)                   =>
        target match {
          case SCALA      => entities(entityId).entityName.pascalCase
          case POSTGRESQL => UNDEFINED_DATA_TYPE
          case _          => UNDEFINED_DATA_TYPE
        }
      case DataElementType(dataElementId)         =>
        getTargetDataType(dataElements(dataElementId).dataType, target)
      case ListCollection(dataType)               =>
        target match {
          case SCALA      => s"Seq[${getTargetDataType(dataType, target)}]"
          case POSTGRESQL => UNDEFINED_DATA_TYPE
          case _          => UNDEFINED_DATA_TYPE
        }
      case SetCollection(dataType)                =>
        target match {
          case SCALA      => s"Set[${getTargetDataType(dataType, target)}]"
          case POSTGRESQL => UNDEFINED_DATA_TYPE
          case _          => UNDEFINED_DATA_TYPE
        }
      case StringMapCollection(dataType)          =>
        target match {
          case SCALA      => s"Map[String, ${getTargetDataType(dataType, target)}]"
          case POSTGRESQL => UNDEFINED_DATA_TYPE
          case _          => UNDEFINED_DATA_TYPE
        }
    }

  def getEntityLabel(raw: Entity, labelId: String): Option[String] =
    raw.labels.get(labelId).orElse(getComponentLabel(raw.componentId, labelId))

  def getComponentLabel(componentId: String, labelId: String): Option[String] =
    components
      .get(componentId)
      .flatMap(c =>
        c.labels
          .get(labelId)
          .orElse(c.parent.flatMap(p => getComponentLabel(p, labelId))),
      )
      .orElse(getDomainLabel(labelId))

  def getDomainLabel(labelId: String): Option[String] =
    labels.get(labelId)
}
