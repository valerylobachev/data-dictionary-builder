package biz.lobachev.annette.data_dictionary.builder.model

case class EntityIndex(
  id: String,
  name: String,
  description: String = "",
  unique: Boolean = false,
  fields: Seq[String]
)
