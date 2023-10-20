package biz.lobachev.annette.data_dictionary.builder.model

import biz.lobachev.annette.data_dictionary.builder.helper.TablePrefixSuffix.{TABLE_NAME_PREFIX, TABLE_NAME_SUFFIX}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax.wrapQuotes

import scala.collection.immutable.ListMap

sealed trait EntityType

case object TableEntity    extends EntityType
case object StructEntity   extends EntityType
case object EmbeddedEntity extends EntityType

case class Entity(
  id: String,
  groupId: String = "",
  name: String,
  description: String = "",
  entityName: String,
  tableName: String,
  entityType: EntityType,
  fields: Seq[EntityField] = Seq.empty,
  pk: Seq[String] = Seq.empty,
  indexes: ListMap[String, EntityIndex] = ListMap.empty,
  relations: Seq[EntityRelation] = Seq.empty,
  schema: Option[String] = None,
  attributes: Attributes = Map.empty,
) {

  def withTableName(tableName: String) = copy(tableName = tableName)

  def withDescription(description: String) = copy(description = description)

  def withFields(seq: EntityField*) = copy(fields = fields ++ seq)

  def withPK(seq: EntityField*) = copy(fields = seq, pk = seq.map(_.fieldName))

  def withIndexes(seq: EntityIndex*) = copy(indexes = ListMap.from(seq.map(e => e.id -> e)))

  def withRelations(seq: EntityRelation*) = copy(relations = seq)

  def withAttributes(seq: Attribute*) = copy(attributes = attributes ++ seq.map(a => a.key -> a.value))

  def fullTableName(): String =
    Seq(attributes.get(TABLE_NAME_PREFIX), Some(tableName), attributes.get(TABLE_NAME_SUFFIX)).flatten.mkString("_")

  def tableNameWithSchema(logical: Boolean = false): String =
    if (logical && name.trim.nonEmpty)
      wrapQuotes(name)
    else
      Seq(schema.map(wrapQuotes), Some(wrapQuotes(fullTableName()))).flatten.mkString(".")

}
