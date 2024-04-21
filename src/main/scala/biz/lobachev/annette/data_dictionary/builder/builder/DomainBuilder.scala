package biz.lobachev.annette.data_dictionary.builder.builder

import biz.lobachev.annette.data_dictionary.builder.labels.TablePrefixSuffix
import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._

import scala.collection.immutable.ListMap

object DomainBuilder extends ModelValidator {

  def build(domain: Domain): Either[Seq[String], Domain] =
    buildStage1(domain).map { d =>
      buildStage2(d)
    }

  def buildStage2(domain: Domain): Domain = {
    val newEntities = domain.entities.map { case k -> entity =>
      val expandedEntity = expandEntity(domain, entity)
      val tableName      = getTablePrefix(domain, entity) + entity.tableName + getTableSuffix(domain, entity)
      val newEntity      = entity.copy(
        tableName = tableName,
        expandedFields = expandedEntity.fields,
        expandedRelations = entity.relations ++ expandedEntity.relations,
      )
      k -> newEntity
    }
    domain.copy(entities = newEntities)
  }

  def expandEntity(domain: Domain, entity: Entity): ExpandedEntity = {
    val r = entity.fields.map { field =>
      field.dataType match {
        case EmbeddedEntityType(entityId, withPrefix, withRelations) =>
          val embeddedEntity = domain.entities(entityId)
          val fr             = expandEntity(domain, domain.entities(entityId))
          val relations      =
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
          val fields         = fr.fields.map { f =>
            val fieldName   =
              if (withPrefix) field.fieldName + f.fieldName.pascalCase else f.fieldName
            val dbFieldName =
              if (withPrefix) field.dbFieldName + "_" + f.dbFieldName else f.dbFieldName
            f.copy(fieldName = fieldName, dbFieldName = dbFieldName)
          }
          ExpandedEntity(fields, relations)
        case _                                                       =>
          ExpandedEntity(Seq(field), Seq.empty)
      }
    }
    ExpandedEntity(
      r.flatMap(_.fields),
      r.flatMap(_.relations),
    )
  }

  def buildStage1(domain: Domain): Either[Seq[String], Domain] = {
    val newEntities = domain.entities.values.map { entity =>
      val newFields = entity.fields.map { field =>
        field.dataType match {
          case DataElementType(dataElementId) =>
            val fieldName   = if (field.fieldName.isEmpty) getFieldName(domain, dataElementId) else field.fieldName
            val dbFieldName =
              if (field.dbFieldName.isEmpty) getDbFieldName(domain, dataElementId) else field.dbFieldName
            val name        = if (field.name.isEmpty) getName(domain, dataElementId) else field.name
            val description =
              if (field.description.isEmpty) getDescription(domain, dataElementId) else field.description
            field.copy(fieldName = fieldName, dbFieldName = dbFieldName, name = name, description = description)
          case _                              =>
            field
        }
      }
      entity.copy(fields = newFields)
    }
    validate(domain.copy(entities = ListMap.from(newEntities.map(e => e.id -> e))))
  }

  def getFieldName(domain: Domain, dataElementId: String): String = {
    val dataElement = domain.dataElements(dataElementId)
    dataElement.dataType match {
      case DataElementType(dataElementId) if dataElement.fieldName.isEmpty =>
        getFieldName(domain, dataElementId)
      case _                                                               =>
        dataElement.fieldName
    }
  }

  def getDbFieldName(domain: Domain, dataElementId: String): String = {
    val dataElement = domain.dataElements(dataElementId)
    dataElement.dataType match {
      case DataElementType(dataElementId) if dataElement.dbFieldName.isEmpty =>
        getDbFieldName(domain, dataElementId)
      case _                                                                 =>
        dataElement.dbFieldName
    }
  }

  def getName(domain: Domain, dataElementId: String): String = {
    val dataElement = domain.dataElements(dataElementId)
    dataElement.dataType match {
      case DataElementType(dataElementId) if dataElement.name.isEmpty =>
        getName(domain, dataElementId)
      case _                                                          =>
        dataElement.name
    }
  }

  def getDescription(domain: Domain, dataElementId: String): String = {
    val dataElement = domain.dataElements(dataElementId)
    dataElement.dataType match {
      case DataElementType(dataElementId) if dataElement.description.isEmpty =>
        getDescription(domain, dataElementId)
      case _                                                                 =>
        dataElement.description
    }
  }

  private def getTablePrefix(domain: Domain, raw: Entity): String =
    domain.getEntityLabel(raw, TablePrefixSuffix.TABLE_NAME_PREFIX).map(s => s"${s}_").getOrElse("")

  private def getTableSuffix(domain: Domain, raw: Entity): String =
    domain.getEntityLabel(raw, TablePrefixSuffix.TABLE_NAME_SUFFIX).map(s => s"_$s").getOrElse("")

}
