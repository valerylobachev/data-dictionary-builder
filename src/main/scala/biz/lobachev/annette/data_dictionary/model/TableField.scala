package biz.lobachev.annette.data_dictionary.model

case class TableField(
  fieldName: String,
  name: String,
  description: String = "",
  labels: Labels = Map.empty,
  dbFieldName: String,
  datatype: Datatype,
  notNull: Boolean = true,
  default: Option[Default] = None,
  generated: Option[Generated] = None,
)

sealed trait Default
case class DefaultValue(value: String)         extends Default
case class DefaultStatement(statement: String) extends Default

sealed trait Generated
case object GeneratedAlwaysAsIdentity                                           extends Generated
case object GeneratedByDefaultAsIdentity                                        extends Generated
case class GeneratedAlwaysAs(statement: String, opt: GeneratedAsStore = Stored) extends Generated

sealed trait GeneratedAsStore
case object Stored  extends GeneratedAsStore
case object Virtual extends GeneratedAsStore
