package biz.lobachev.annette.data_dictionary.generators.golang

import biz.lobachev.annette.data_dictionary.model.Struct

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

case class GoStructsData(
  filename: String,
  pkg: String,
  lastPkg: String,
  imports: Seq[GoImport],
  structs: Seq[GoStructData],
  date: String = OffsetDateTime.now().toLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
)

case class GoStructData(
  comments: Seq[String],
  name: String,
  structName: String,
  members: Seq[GoStructMember],
)

case class GoStructPackage(
  pkg: String,
  files: Map[String, Seq[GoStructPackageFile]],
)

case class GoStructPackageFile(
  pkg: String,
  filename: String,
  struct: Struct,
)
