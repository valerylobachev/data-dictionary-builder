package biz.lobachev.annette.data_dictionary.builder.rendering.kotlin

import biz.lobachev.annette.data_dictionary.builder.model.EntityField

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

case class KtClass(
  pkg: String,
  imports: Seq[String],
  comments: Seq[String],
  annotations: Seq[String],
  name: String,
  members: Seq[KtClassMember],
  extensions: Option[String],
  date: String = OffsetDateTime.now().toLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
)

case class KtClassMember(
                          comments: Seq[String],
                          annotations: Seq[String],
                          name: String,
                          datatype: String,
                          defaultValue: Option[String],
                          field: EntityField,
)
