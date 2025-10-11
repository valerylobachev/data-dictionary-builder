package biz.lobachev.annette.data_dictionary.dsl

import biz.lobachev.annette.data_dictionary.model.Datatype

sealed trait StructFieldDef

case class IncludeStructFields(includeId: String) extends StructFieldDef

case class StructFieldDefinition(
  fieldName: String,
  name: String,
  description: String = "",
  labels: Seq[Label] = Seq.empty,
  datatype: Datatype,
  optional: Boolean = false,
) extends StructFieldDef {
  def withDescription(description: String) = copy(description = description)
  def withLabels(seq: Label*)              = copy(labels = labels ++ seq)
}
