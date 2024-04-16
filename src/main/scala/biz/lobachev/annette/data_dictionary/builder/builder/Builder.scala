package biz.lobachev.annette.data_dictionary.builder.builder

import biz.lobachev.annette.data_dictionary.builder.labels.TablePrefixSuffix
import biz.lobachev.annette.data_dictionary.builder.model.{
  Component,
  DataElement,
  Domain,
  EmbeddedEntityType,
  Entity,
  EntityField,
  EntityIndex,
  EntityRelation,
  EnumData,
  ErrorStatus,
  ExpandedEntity,
  Message,
  RawComponent,
  RawDataElement,
  RawDomain,
  RawEntity,
  RawEntityField,
  RawEntityIndex,
  RawEntityRelation,
  RawEnumData,
}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._

import scala.collection.immutable.ListMap

class Builder(val rawDomain: RawDomain) {

  private val messages: Seq[Message] = Seq.empty

  def build(): Either[Seq[Message], Domain] = {
    val componentData = rawDomain.rootComponentIds.map(componentId => processComponent(componentId))
    val domain        = Domain(
      id = rawDomain.id,
      name = rawDomain.name,
      description = rawDomain.description,
      rootComponentIds = rawDomain.rootComponentIds,
      components = ListMap.from(componentData.flatMap(_.components).map(c => c.id -> c)),
      entities = ListMap.from(componentData.flatMap(_.entities).map(c => c.id -> c)),
      dataElements = ListMap.from(componentData.flatMap(_.dataElements).map(c => c.id -> c)),
      enums = ListMap.from(componentData.flatMap(_.enums).map(c => c.id -> c)),
      raw = rawDomain,
      messages = messages,
    )
    if (messages.map(_.status).contains(ErrorStatus)) {
      Left(messages)
    } else {
      Right(domain)
    }

  }

  private def processComponent(componentId: String): ComponentData = {
    val component = convertComponent(rawDomain.components(componentId))

    val entities     = rawDomain.entities.values.filter(_.componentId == Some(componentId)).map(processEntity)
    val dataElements = rawDomain.dataElements.values.filter(_.componentId == Some(componentId)).map(convertDataElement)
    val enums        = rawDomain.enums.values.filter(_.componentId == Some(componentId)).map(convertEnum)
    val components   = component.children.map(processComponent(_))

    val data = ComponentData(
      components = Seq(component) ++ components.flatMap(_.components),
      entities = (entities ++ components.flatMap(_.entities)).toSeq,
      dataElements = (dataElements ++ components.flatMap(_.dataElements)).toSeq,
      enums = (enums ++ components.flatMap(_.enums)).toSeq,
    )
    data
  }

  private def convertComponent(raw: RawComponent): Component =
    Component(
      id = raw.id,
      name = raw.name,
      description = raw.description,
      parent = raw.parent,
      children = raw.children,
      schema = raw.schema,
      labels = raw.labels,
      raw: RawComponent,
    )

  private def processEntity(raw: RawEntity): Entity = {
    val tableName      = getTablePrefix(raw) + raw.tableName + getTableSuffix(raw)
    val expandedEntity = expandEntity(raw)
    val fields         = convertFields(expandedEntity.fields)
    val indexes        = convertIndexes(raw.indexes)
    val relations      = convertRelations(raw.relations ++ expandedEntity.relations)
    Entity(
      id = raw.id,
      componentId = raw.componentId,
      name = raw.name,
      description = raw.description,
      entityName = raw.entityName,
      tableName = tableName,
      entityType = raw.entityType,
      fields = fields,
      pk = raw.pk,
      indexes = indexes,
      relations = relations,
      schema = raw.schema,
      labels = raw.labels,
      raw = raw,
    )
  }

  private def convertIndexes(indexes: Seq[RawEntityIndex]) =
    indexes.map(i =>
      EntityIndex(
        id = i.id,
        name = i.name,
        description = i.description,
        unique = i.unique,
        fields = i.fields,
        labels = i.labels,
        raw = i,
      ),
    )

  private def convertRelations(relations: Seq[RawEntityRelation]) =
    relations.map(r =>
      EntityRelation(
        id = r.id,
        name = r.name,
        description = r.description,
        relationType = r.relationType,
        referenceEntityId = r.referenceEntityId,
        fields = r.fields,
        onUpdate = r.onUpdate,
        onDelete = r.onDelete,
        labels = r.labels,
        raw = r,
      ),
    )

  private def convertFields(fields: Seq[RawEntityField]) =
    fields.map(f =>
      EntityField(
        name = f.name,
        description = f.description,
        fieldName = f.fieldName,
        dbFieldName = f.dbFieldName,
        dataType = f.dataType,
        notNull = f.notNull,
        autoIncrement = f.autoIncrement,
        labels = f.labels,
        raw = f,
      ),
    )

  def expandEntity(entity: RawEntity): ExpandedEntity = {
    val r = entity.fields.map { field =>
      field.dataType match {
        case EmbeddedEntityType(entityId, withPrefix, withRelations) =>
          val embeddedEntity = rawDomain.entities(entityId)
          val fr             = expandEntity(rawDomain.entities(entityId))
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

  private def convertDataElement(raw: RawDataElement): DataElement =
    DataElement(
      id = raw.id,
      name = raw.name,
      description = raw.description,
      fieldName = raw.fieldName,
      dbFieldName = raw.dbFieldName,
      dataType = raw.dataType,
      notNull = raw.notNull,
      labels = raw.labels,
      componentId = raw.componentId,
      raw = raw,
    )

  private def convertEnum(raw: RawEnumData): EnumData =
    EnumData(
      id = raw.id,
      name = raw.name,
      description = raw.description,
      length = raw.length,
      elements = raw.elements,
      labels = raw.labels,
      raw = raw,
    )

  private def getTablePrefix(raw: RawEntity): String =
    getEntityLabel(raw, TablePrefixSuffix.TABLE_NAME_PREFIX).map(s => s"${s}_").getOrElse("")

  private def getTableSuffix(raw: RawEntity): String =
    getEntityLabel(raw, TablePrefixSuffix.TABLE_NAME_SUFFIX).map(s => s"_$s").getOrElse("")

  private def getEntityLabel(raw: RawEntity, labelId: String): Option[String] =
    raw.labels.get(labelId).orElse(getComponentLabel(raw.componentId, labelId))

  private def getComponentLabel(componentId: String, labelId: String): Option[String] =
    rawDomain.components
      .get(componentId)
      .flatMap(c =>
        c.labels
          .get(labelId)
          .orElse(c.parent.flatMap(p => getComponentLabel(p, labelId))),
      )

}
