package biz.lobachev.annette.data_dictionary.builder.model

import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax.wrapQuotes

case class Entity(
  id: String,
  componentId: String = "",
  name: String,
  description: String = "",
  entityName: String,
  tableName: String,
  entityType: EntityType,
  fields: Seq[EntityField] = Seq.empty,
  pk: Seq[String] = Seq.empty,
  indexes: Seq[EntityIndex] = Seq.empty,
  relations: Seq[EntityRelation] = Seq.empty,
  schema: Option[String] = None,
  labels: Labels = Map.empty,
  expandedFields: Seq[EntityField] = Seq.empty,
  expandedRelations: Seq[EntityRelation] = Seq.empty,
) {

  def withTableName(tableName: String) = copy(tableName = tableName)

  def withDescription(description: String) = copy(description = description)

  def withFields(seq: EntityField*) = copy(fields = fields ++ seq)

  def withPK(seq: EntityField*) = copy(fields = seq, pk = seq.map(_.fieldName))

  def withIndexes(seq: EntityIndex*) = copy(indexes = seq)

  def withRelations(seq: EntityRelation*) = copy(relations = seq)

  def withLabels(seq: Label*) = copy(labels = labels ++ seq.map(a => a.key -> a.value))

  def tableNameWithSchema(logical: Boolean = false): String =
    if (logical && name.trim.nonEmpty)
      wrapQuotes(name)
    else
      Seq(schema.map(wrapQuotes), Some(wrapQuotes(tableName))).flatten.mkString(".")

}
