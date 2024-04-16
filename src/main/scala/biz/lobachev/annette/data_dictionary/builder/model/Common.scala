package biz.lobachev.annette.data_dictionary.builder.model

sealed trait RelationType
case object ManyToOne extends RelationType
case object OneToOne  extends RelationType

case class ExpandedEntity(fields: Seq[RawEntityField], relations: Seq[RawEntityRelation])

sealed trait EntityType
case object TableEntity    extends EntityType
case object StructEntity   extends EntityType
case object EmbeddedEntity extends EntityType

sealed trait ForeignKeyAction
case object Cascade    extends ForeignKeyAction
case object Restrict   extends ForeignKeyAction
case object NoAction   extends ForeignKeyAction
case object SetNull    extends ForeignKeyAction
case object SetDefault extends ForeignKeyAction
