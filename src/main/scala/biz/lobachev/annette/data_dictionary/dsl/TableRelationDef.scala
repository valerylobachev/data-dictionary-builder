package biz.lobachev.annette.data_dictionary.dsl

import biz.lobachev.annette.data_dictionary.model.{Association, ForeignKeyAction, NoAction, RelationType}

case class TableRelationDef(
  id: String,
  name: String,
  description: String = "",
  labels: Seq[Label] = Seq.empty,
  relationType: RelationType,
  referenceTableId: String,
  fields: Seq[(String, String)],
  onUpdate: ForeignKeyAction = NoAction,
  onDelete: ForeignKeyAction = NoAction,
  logical: Boolean = false,
  association: Option[Association] = None,
) {
  def withDescription(description: String) = copy(description = description)

  def withLabels(seq: Label*) = copy(labels = labels ++ seq)

  def withOnUpdate(act: ForeignKeyAction) = copy(onUpdate = act)

  def withOnDelete(act: ForeignKeyAction) = copy(onDelete = act)

  def withAssociation(name: String) = copy(association = Some(Association(name)))

  def withAssociation(assoc: (String, String)) = copy(association = Some(Association(assoc._1, Some(assoc._2))))

}
