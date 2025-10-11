package biz.lobachev.annette.data_dictionary.model

case class TableRelation(
  id: String,
  name: String,
  description: String = "",
  labels: Labels = Map.empty,
  relationType: RelationType,
  referenceTableId: String,
  fields: Seq[(String, String)],
  onUpdate: ForeignKeyAction = NoAction,
  onDelete: ForeignKeyAction = NoAction,
  logical: Boolean = false,
  association: Option[Association] = None,
)

case class Association(
  name: String,
  referenceName: Option[String] = None,
)

sealed trait RelationType
case object ManyToOne extends RelationType
case object OneToOne  extends RelationType

sealed trait ForeignKeyAction {
  def sqlAction(): String
}
case object Cascade    extends ForeignKeyAction {
  override def sqlAction(): String = "CASCADE"
}
case object Restrict   extends ForeignKeyAction {
  override def sqlAction(): String = "RESTRICT"
}
case object NoAction   extends ForeignKeyAction {
  override def sqlAction(): String = "NO ACTION"
}
case object SetNull    extends ForeignKeyAction {
  override def sqlAction(): String = "SET NULL"
}
case object SetDefault extends ForeignKeyAction {
  override def sqlAction(): String = "SET DEFAULT"
}
