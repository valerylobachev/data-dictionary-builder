package biz.lobachev.annette.data_dictionary.dsl

import biz.lobachev.annette.data_dictionary.model.{Datatype, Default, Generated}

sealed trait TableFieldDef

case class IncludeTableFields(includeId: String) extends TableFieldDef

case class TableFieldDefinition(
  fieldName: String,
  name: String,
  description: String = "",
  labels: Seq[Label] = Seq.empty,
  dbFieldName: String,
  datatype: Datatype,
  notNull: Boolean = true,
  default: Option[Default] = None,
  generated: Option[Generated] = None
) extends TableFieldDef {
  def withLabels(seq: Label*) = copy(labels = labels ++ seq)

}
