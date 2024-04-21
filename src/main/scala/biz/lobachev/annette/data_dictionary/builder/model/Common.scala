package biz.lobachev.annette.data_dictionary.builder.model

sealed trait RelationType
case object ManyToOne extends RelationType
case object OneToOne  extends RelationType

case class ExpandedEntity(fields: Seq[EntityField], relations: Seq[EntityRelation])

sealed trait EntityType
case object TableEntity    extends EntityType
case object StructEntity   extends EntityType
case object EmbeddedEntity extends EntityType

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
