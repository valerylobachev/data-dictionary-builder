package biz.lobachev.annette.data_dictionary.model

import biz.lobachev.annette.data_dictionary.model.*

case class Table(
  id: String,
  name: String,
  description: String = "",
  componentId: Option[String] = None,
  labels: Labels = Map.empty,
  entityName: String,
  tableName: String,
  fields: Seq[TableField] = Seq.empty,
  pk: Seq[String] = Seq.empty,
  indexes: Seq[TableIndex] = Seq.empty,
  relations: Seq[TableRelation] = Seq.empty,
)
