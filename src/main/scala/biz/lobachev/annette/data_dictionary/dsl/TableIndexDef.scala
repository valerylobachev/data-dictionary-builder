package biz.lobachev.annette.data_dictionary.dsl

import biz.lobachev.annette.data_dictionary.model.{IndexField, IndexMethod}

case class TableIndexDef(
  id: String,
  name: String,
  description: String = "",
  labels: Seq[Label] = Seq.empty,
  unique: Boolean = false,
  fields: Seq[IndexField],
  method: Option[IndexMethod] = None,
  condition: Option[String] = None,
  includeFields: Seq[String] = Seq.empty,
) {

  def withDescription(description: String) = copy(description = description)
  def withLabels(seq: Label*)              = copy(labels = labels ++ seq)
  def withMethod(m: IndexMethod)           = copy(method = Some(m))
  def where(c: String)                     = copy(condition = Some(c))
  def include(seq: String*)                = copy(includeFields = includeFields ++ seq)

}
