package biz.lobachev.annette.data_dictionary.builder.model

sealed trait ForeignKeyAction

case object Cascade    extends ForeignKeyAction
case object Restrict   extends ForeignKeyAction
case object NoAction   extends ForeignKeyAction
case object SetNull    extends ForeignKeyAction
case object SetDefault extends ForeignKeyAction

case class EntityRelation(
  id: String,
  name: String,
  description: String = "",
  relationType: RelationType,
  referenceEntityId: String,
  fields: Seq[(String, String)],
  onUpdate: ForeignKeyAction = NoAction,
  onDelete: ForeignKeyAction = NoAction,
  attributes: Attributes = Map.empty
) {
  def withFields(seq: (String, String)*) = copy(fields = seq)

  def withAttributes(seq: Attribute*) = copy(attributes = attributes ++ seq.map(a => a.key -> a.value))
}

sealed trait RelationType

case object ManyToOne extends RelationType
case object OneToOne  extends RelationType
