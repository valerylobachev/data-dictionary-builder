package biz.lobachev.annette.data_dictionary.builder.rendering.golang

import biz.lobachev.annette.data_dictionary.builder.model.EntityField

case class GoStruct(
  pkg: String,
  lastPkg: String,
  imports: Seq[String],
  comments: Seq[String],
  name: String,
  entityName: String,
  tableName: String,
  isGorm: Boolean,
  schemaName: Option[String],
  members: Seq[KtStructMember],
  constants: Seq[Constant],
)

case class KtStructMember(
  comments: Seq[String],
  name: String,
  datatype: String,
  tags: String,
  field: EntityField,
)

case class Constant(
  key: String,
  value: String,
)
