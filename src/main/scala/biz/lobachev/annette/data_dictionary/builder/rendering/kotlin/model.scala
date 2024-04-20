package biz.lobachev.annette.data_dictionary.builder.rendering.kotlin

import biz.lobachev.annette.data_dictionary.builder.model.EntityField

case class KtClass(
  pkg: String,
  imports: Seq[String],
  comments: Seq[String],
  annotations: Seq[String],
  name: String,
  members: Seq[KtClassMember],
  extensions: Option[String],
)

case class KtClassMember(
                          comments: Seq[String],
                          annotations: Seq[String],
                          name: String,
                          datatype: String,
                          defaultValue: Option[String],
                          field: EntityField,
)
