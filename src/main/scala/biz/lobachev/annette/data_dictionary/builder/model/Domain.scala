package biz.lobachev.annette.data_dictionary.builder.model

import scala.collection.immutable.ListMap

case class Domain(
  id: String,
  name: String,
  description: String,
  rootComponents: Seq[String] = Seq.empty,
  components: ListMap[String, Component] = ListMap.empty,
  entities: ListMap[String, Entity] = ListMap.empty,
  dataElements: ListMap[String, DataElement] = ListMap.empty,
  enums: ListMap[String, EnumData] = ListMap.empty,
  labels: Labels = Map.empty,
  raw: RawDomain,
)

case class Component(
  id: String,
  name: String,
  description: String,
  parent: Option[String] = None,
  children: Seq[String] = Seq.empty,
  schema: Option[String] = None,
  labels: Labels = Map.empty,
  raw: RawComponent,
)

case class Entity(
  id: String,
  componentId: String,
  name: String,
  description: String,
  entityName: String,
  tableName: String,
  entityType: EntityType,
  fields: Seq[EntityField] = Seq.empty,
  pk: Seq[String] = Seq.empty,
  indexes: ListMap[String, EntityIndex] = ListMap.empty,
  relations: Seq[EntityRelation] = Seq.empty,
  schema: Option[String] = None,
  labels: Labels = Map.empty,
  raw: RawEntity,
)

case class EntityField(
  name: String,
  description: String,
  fieldName: String,
  dbFieldName: String,
  dataType: DataType,
  notNull: Boolean = true,
  autoIncrement: Boolean = false,
  labels: Labels = Map.empty,
  raw: RawEntityField,
)

case class EntityIndex(
  id: String,
  name: String,
  description: String,
  unique: Boolean = false,
  fields: Seq[String],
  labels: Labels = Map.empty,
  raw: RawEntityIndex,
)

case class EntityRelation(
  id: String,
  name: String,
  description: String,
  relationType: RelationType,
  referenceEntityId: String,
  fields: Seq[(String, String)],
  onUpdate: ForeignKeyAction = NoAction,
  onDelete: ForeignKeyAction = NoAction,
  labels: Labels = Map.empty,
  raw: RawEntityRelation,
)

case class DataElement(
  id: String,
  name: String,
  description: String,
  fieldName: String,
  dbFieldName: String,
  dataType: DataType,
  notNull: Boolean = true,
  labels: Labels = Map.empty,
  componentId: Option[String] = None,
  raw: RawDataElement,
)

case class EnumData(
  id: String,
  name: String,
  description: String,
  length: Int,
  elements: Seq[(String, String)],
  labels: Labels = Map.empty,
  raw: RawEnumData,
)
