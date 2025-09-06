package biz.lobachev.annette.data_dictionary.builder.model

import biz.lobachev.annette.data_dictionary.builder.model.*

case class DataModel(
  id: String,
  modelName: String,
  name: String,
  description: String = "",
  labels: Labels = Map.empty,
  likeEntity: Option[LikeEntity] = None,
  fields: Seq[ModelField] = Seq.empty,
  componentId: Option[String] = None,
) {
  def withDescription(description: String) = copy(description = description)

  def withLabels(seq: Label*) = copy(labels = labels ++ seq.map(a => a.key -> a.value))

  def likeEntity(entityId: String, fieldUsage: FieldUsage = IncludeAllFields) =
    copy(likeEntity = Some(LikeEntity(entityId, fieldUsage)))

  def likeEntityPK(entityId: String) =
    copy(likeEntity = Some(LikeEntity(entityId, IncludePKFields)))

  def withFields(seq: ModelField*) = copy(fields = fields ++ seq)

}

case class LikeEntity(
  entityId: String,
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

case class ModelField(
  fieldName: String,
  name: String,
  description: String = "",
  datatype: FieldDatatype,
  optional: Boolean = false,
  labels: Labels = Map.empty,
)
