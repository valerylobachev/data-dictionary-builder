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
  schema: Option[String] = None
) {
  def fullTableName(): String =
    Seq(schema.map(wrapQuotes), Some(wrapQuotes(tableName))).flatten.mkString(".")
}
