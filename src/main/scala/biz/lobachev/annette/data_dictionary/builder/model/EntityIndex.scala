package biz.lobachev.annette.data_dictionary.builder.model

case class EntityIndex(
  id: String,
  name: String,
  description: String = "",
  unique: Boolean = false,
  fields: Seq[String],
  attributes: Attributes = Map.empty,
) {
  def withDescription(description: String) = copy(description = description)

  def withAttributes(seq: Attribute*) = copy(attributes = attributes ++ seq.map(a => a.key -> a.value))
}
