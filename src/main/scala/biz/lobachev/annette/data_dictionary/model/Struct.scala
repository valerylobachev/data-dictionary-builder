package biz.lobachev.annette.data_dictionary.model

import biz.lobachev.annette.data_dictionary.model.*

case class Struct(
  id: String,
  name: String,
  description: String = "",
  componentId: Option[String] = None,
  labels: Labels = Map.empty,
  structName: String,
  likeTable: Option[LikeTable] = None,
  fields: Seq[StructField] = Seq.empty,
)

case class LikeTable(
  tableId: String,
  fieldUsage: FieldUsage = IncludeAllFields,
) {
  def including(seq: String*) = copy(fieldUsage = IncludeFields(seq))
  def excluding(seq: String*) = copy(fieldUsage = ExcludeFields(seq))
}

sealed trait FieldUsage
case object IncludeAllFields                  extends FieldUsage
case object IncludePKFields                   extends FieldUsage
case class IncludeFields(fields: Seq[String]) extends FieldUsage
case class ExcludeFields(fields: Seq[String]) extends FieldUsage
