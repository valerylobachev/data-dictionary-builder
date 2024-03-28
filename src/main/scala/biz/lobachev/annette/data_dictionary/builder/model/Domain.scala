package biz.lobachev.annette.data_dictionary.builder.model

import biz.lobachev.annette.data_dictionary.builder.{POSTGRESQL, SCALA}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._
import biz.lobachev.annette.data_dictionary.builder.utils.Utils

import scala.collection.immutable.ListMap

case class Domain(
  id: String,
  name: String,
  description: String = "",
  rootComponents: Seq[String] = Seq.empty,
  components: ListMap[String, Component] = ListMap.empty,
  entities: ListMap[String, Entity] = ListMap.empty,
  dataElements: ListMap[String, DataElement] = ListMap.empty,
  enums: ListMap[String, EnumData] = ListMap.empty,
  labels: Labels = Map.empty,
) extends ModelValidator {

  def withComponentSeq(componentSeq: Seq[ComponentData]): Domain = {
    val newRootComponentIds   = componentSeq.map(_.component.id)
    val newComponents         = componentSeq.flatMap(_.expandComponents())
    val newEntities           = componentSeq.flatMap(_.expandEntities())
    val newDataElements       = componentSeq.flatMap(_.expandDataElements())
    val componentDuplicates   = Utils.findDuplicates(components.keys.toSeq ++ newComponents.map(_.id))
    val entityDuplicates      = Utils.findDuplicates(entities.keys.toSeq ++ newEntities.map(_.id))
    val dataElementDuplicates = Utils.findDuplicates(dataElements.keys.toSeq ++ newDataElements.map(_.id))
    if (componentDuplicates.nonEmpty) println("Duplicated components: " + componentDuplicates.mkString(", "))
    if (entityDuplicates.nonEmpty) println("Duplicated entities: " + entityDuplicates.mkString(", "))
    if (dataElementDuplicates.nonEmpty) println("Duplicated data elements: " + dataElementDuplicates.mkString(", "))
    if (
      componentDuplicates.nonEmpty ||
      entityDuplicates.nonEmpty ||
      dataElementDuplicates.nonEmpty
    ) throw new IllegalArgumentException("Duplicated items")
    copy(
      rootComponents = rootComponents ++ newRootComponentIds,
      components = components ++ newComponents.map(c => c.id -> c),
      entities = entities ++ newEntities.map(e => e.id -> e),
      dataElements = dataElements ++ newDataElements.map(e => e.id -> e),
    )
  }

  def withComponents(componentSeq: ComponentData*) = withComponentSeq(componentSeq)

  def withDataElementSeq(seq: Seq[DataElement]) = {
    val dataElementDuplicates = Utils.findDuplicates(seq.map(_.id))
    if (dataElementDuplicates.nonEmpty) println("Duplicated data elements: " + dataElementDuplicates.mkString(", "))
    if (dataElementDuplicates.nonEmpty) throw new IllegalArgumentException("Duplicated items")
    copy(dataElements = ListMap.from(seq.map(e => e.id -> e)))
  }

  def withDataElements(seq: DataElement*) = withDataElementSeq(seq)

  def withEnumSeq(seq: Seq[EnumData]) =
    copy(enums = ListMap.from(seq.map(e => e.id -> e)))

  def withEnums(seq: EnumData*) = withEnumSeq(seq)

  def withLabels(seq: Label*) = copy(labels = labels ++ seq.map(a => a.key -> a.value))

  def build(replicateAttr: Boolean = true): Either[Seq[String], Domain] = {
    val newEntities = entities.values.map { entity =>
      val newFields = entity.fields.map { field =>
        field.dataType match {
          case DataElementType(dataElementId) =>
            val fieldName   = if (field.fieldName.isEmpty) getFieldName(dataElementId) else field.fieldName
            val dbFieldName = if (field.dbFieldName.isEmpty) getDbFieldName(dataElementId) else field.dbFieldName
            val name        = if (field.name.isEmpty) getName(dataElementId) else field.name
            val description = if (field.description.isEmpty) getDescription(dataElementId) else field.description
            field.copy(fieldName = fieldName, dbFieldName = dbFieldName, name = name, description = description)
          case _                              =>
            field
        }
      }
      entity.copy(fields = newFields)
    }
    val res         = copy(entities = ListMap.from(newEntities.map(e => e.id -> e)))
    val err         = res.validate()
    if (err.isEmpty) {
      if (replicateAttr) Right(res.replicateLabels())
      else Right(res)
    } else Left(err)
  }

  def replicateLabels(): Domain = {
    val domainLabels    = labels
    val newComponents   = components.map { case key -> component =>
      key -> component.copy(labels = domainLabels ++ component.labels)
    }
    val newDataElements = dataElements.map { case key -> dataElement =>
      key -> dataElement.copy(labels = domainLabels ++ dataElement.labels)
    }
    val newEnums        = enums.map { case key -> enum =>
      key -> enum.copy(labels = domainLabels ++ enum.labels)
    }
    val newEntities     = entities.map { case key -> entity =>
      val componentLabels = newComponents(entity.componentId).labels
      key -> entity.copy(labels = componentLabels ++ entity.labels)
    }
    copy(components = newComponents, entities = newEntities, dataElements = newDataElements, enums = newEnums)
  }

  def rolloutEntity(entity: Entity): FieldRelation = {
    val r = entity.fields.map { field =>
      field.dataType match {
        case EmbeddedEntityType(entityId, withPrefix, withRelations) =>
          val embeddedEntity = entities(entityId)

          val fr        = rolloutEntity(entities(entityId))
          val relations =
            if (withRelations)
              (embeddedEntity.relations ++ fr.relations).map { r =>
                if (withPrefix)
                  r.copy(
                    id = field.fieldName + r.id.pascalCase,
                    fields = r.fields.map { case f1 -> f2 => field.fieldName + f1.pascalCase -> f2 },
                  )
                else r
              }
            else Seq.empty
          val fields    = fr.fields.map { f =>
            val fieldName   =
              if (withPrefix) field.fieldName + f.fieldName.pascalCase else f.fieldName
            val dbFieldName =
              if (withPrefix) field.dbFieldName + "_" + f.dbFieldName else f.dbFieldName
            f.copy(fieldName = fieldName, dbFieldName = dbFieldName)
          }
          FieldRelation(fields, relations)
        case _                                                       =>
          FieldRelation(Seq(field), Seq.empty)
      }
    }
    FieldRelation(
      r.flatMap(_.fields),
      r.flatMap(_.relations),
    )
  }

  def rolloutEntityFields(entity: Entity): Seq[EntityField]       = rolloutEntity(entity).fields
  def rolloutEntityRelations(entity: Entity): Seq[EntityRelation] = entity.relations ++ rolloutEntity(entity).relations

//  def rolloutEntityFields(fields: Seq[EntityField]): Seq[EntityField] =
//    fields.flatMap { field =>
//      field.dataType match {
//        case EmbeddedEntityType(entityId, withPrefix, _) =>
//          val rolledOutFields = rolloutEntityFields(entities(entityId).fields)
//          if (withPrefix)
//            rolledOutFields.map(f =>
//              f.copy(
//                fieldName = field.fieldName + f.fieldName.pascalCase,
//                dbFieldName = field.dbFieldName + "_" + f.dbFieldName
//              )
//            )
//          else rolledOutFields
//        case _                                           =>
//          Seq(field)
//      }
//    }

  def getFieldName(dataElementId: String): String = {
    val dataElement = dataElements(dataElementId)
    dataElement.dataType match {
      case DataElementType(dataElementId) if dataElement.fieldName.isEmpty =>
        getFieldName(dataElementId)
      case _                                                               =>
        dataElement.fieldName
    }
  }

  def getDbFieldName(dataElementId: String): String = {
    val dataElement = dataElements(dataElementId)
    dataElement.dataType match {
      case DataElementType(dataElementId) if dataElement.dbFieldName.isEmpty =>
        getDbFieldName(dataElementId)
      case _                                                                 =>
        dataElement.dbFieldName
    }
  }

  def getName(dataElementId: String): String = {
    val dataElement = dataElements(dataElementId)
    dataElement.dataType match {
      case DataElementType(dataElementId) if dataElement.name.isEmpty =>
        getName(dataElementId)
      case _                                                          =>
        dataElement.name
    }
  }

  def getDescription(dataElementId: String): String = {
    val dataElement = dataElements(dataElementId)
    dataElement.dataType match {
      case DataElementType(dataElementId) if dataElement.description.isEmpty =>
        getDescription(dataElementId)
      case _                                                                 =>
        dataElement.description
    }
  }

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

}

case class FieldRelation(fields: Seq[EntityField], relations: Seq[EntityRelation])
