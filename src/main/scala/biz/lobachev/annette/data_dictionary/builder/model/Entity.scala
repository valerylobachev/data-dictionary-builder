package biz.lobachev.annette.data_dictionary.builder.model

import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax.wrapQuotes

import scala.collection.immutable.ListMap

trait EntityType

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
  attributes: Attributes = Seq.empty,
) {

  def withTableName(tableName: String) = copy(tableName = tableName)

  def withDescription(description: String) = copy(description = description)

  def withFields(seq: EntityField*) = copy(fields = fields ++ seq)

  def withPK(seq: EntityField*) = copy(fields = seq, pk = seq.map(_.fieldName))

  def withIndexes(seq: EntityIndex*) = copy(indexes = ListMap.from(seq.map(e => e.id -> e)))

  def withRelations(seq: EntityRelation*) = copy(relations = seq)

  def withAttributes(seq: Attribute*) = copy(attributes = attributes ++ seq)

  def fullTableName(logical: Boolean = false): String =
    if (logical && name.trim.nonEmpty)
      Seq(schema.map(wrapQuotes), Some(wrapQuotes(name))).flatten.mkString(".")
    else
      Seq(schema.map(wrapQuotes), Some(wrapQuotes(tableName))).flatten.mkString(".")
}
