package biz.lobachev.annette.data_dictionary.dsl

import biz.lobachev.annette.data_dictionary.model.*

case class StructDef(
  id: String,
  name: String,
  description: String = "",
  labels: Seq[Label] = Seq.empty,
  structName: String,
  likeTable: Option[LikeTable] = None,
  fields: Seq[StructFieldDef] = Seq.empty,
  componentId: Option[String] = None,
) {
  def withDescription(description: String)                                  = copy(description = description)
  def withLabels(seq: Label*)                                               = copy(labels = labels ++ seq)
  def likeTable(tableId: String, fieldUsage: FieldUsage = IncludeAllFields) =
    copy(likeTable = Some(LikeTable(tableId, fieldUsage)))
  def likeTablePK(tableId: String)                                          =
    copy(likeTable = Some(LikeTable(tableId, IncludePKFields)))

  def withFields(seq: StructFieldDef*) = copy(fields = fields ++ seq)
}
