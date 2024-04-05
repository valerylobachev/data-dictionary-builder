package biz.lobachev.annette.data_dictionary.builder.model

import biz.lobachev.annette.data_dictionary.builder.labels.TablePrefixSuffix.{TABLE_NAME_PREFIX, TABLE_NAME_SUFFIX}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax.wrapQuotes

import scala.collection.immutable.ListMap

case class RawEntity(
  id: String,
  componentId: String = "",
  name: String,
  description: String = "",
  entityName: String,
  tableName: String,
  entityType: EntityType,
  fields: Seq[RawEntityField] = Seq.empty,
  pk: Seq[String] = Seq.empty,
  indexes: ListMap[String, RawEntityIndex] = ListMap.empty,
  relations: Seq[RawEntityRelation] = Seq.empty,
  schema: Option[String] = None,
  labels: Labels = Map.empty,
) {

  def withTableName(tableName: String) = copy(tableName = tableName)

  def withDescription(description: String) = copy(description = description)

  def withFields(seq: RawEntityField*) = copy(fields = fields ++ seq)

  def withPK(seq: RawEntityField*) = copy(fields = seq, pk = seq.map(_.fieldName))

  def withIndexes(seq: RawEntityIndex*) = copy(indexes = ListMap.from(seq.map(e => e.id -> e)))

  def withRelations(seq: RawEntityRelation*) = copy(relations = seq)

  def withLabels(seq: Label*) = copy(labels = labels ++ seq.map(a => a.key -> a.value))

  def fullTableName(): String =
    Seq(labels.get(TABLE_NAME_PREFIX), Some(tableName), labels.get(TABLE_NAME_SUFFIX)).flatten.mkString("_")

  def tableNameWithSchema(logical: Boolean = false): String =
    if (logical && name.trim.nonEmpty)
      wrapQuotes(name)
    else
      Seq(schema.map(wrapQuotes), Some(wrapQuotes(fullTableName()))).flatten.mkString(".")

}
