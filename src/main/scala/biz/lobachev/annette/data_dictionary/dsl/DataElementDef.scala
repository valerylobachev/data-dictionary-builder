package biz.lobachev.annette.data_dictionary.dsl

import biz.lobachev.annette.data_dictionary.model.Datatype

case class DataElementDef(
  id: String,
  name: String,
  description: String = "",
  labels: Seq[Label] = Seq.empty,
  fieldName: String,
  dbFieldName: String,
  datatype: Datatype,
  componentId: Option[String] = None,
) {
  def withDescription(description: String) = copy(description = description)
  def withDbFieldName(fn: String)          = copy(dbFieldName = fn)
  def withLabels(seq: Label*)              = copy(labels = labels ++ seq)
}
