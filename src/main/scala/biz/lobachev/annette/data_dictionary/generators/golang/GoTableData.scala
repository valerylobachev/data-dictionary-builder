package biz.lobachev.annette.data_dictionary.generators.golang

import biz.lobachev.annette.data_dictionary.model.GoPackage

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

case class GoTableData(
  pkg: String,
  lastPkg: String,
  imports: Seq[GoImport],
  comments: Seq[String],
  name: String,
  entityName: String,
  tableName: String,
  isGorm: Boolean,
  schemaName: Option[String],
  members: Seq[GoStructMember],
  ownRelationMembers: Seq[GoStructMember],
  refRelationMembers: Seq[GoStructMember],
  constants: Seq[Constant],
  date: String = OffsetDateTime.now().toLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
)

case class GoImport(
  alias: String,
  pkg: GoPackage,
  useAlias: Boolean,
)

case class GoStructMember(
  comments: Seq[String],
  name: String,
  datatype: String,
  tags: String,
)

case class Constant(
  key: String,
  value: String,
)
