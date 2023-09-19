package biz.lobachev.annette.data_dictionary.builder.model

case class Group(
  id: String,
  name: String,
  description: String = "",
  schema: Option[String] = None,
  attributes: Attributes = Map.empty
) {
  def withSchema(schema: String) = copy(schema = Some(schema))

  def withAttributes(seq: Attribute*) = copy(attributes = attributes ++ seq.map(a => a.key -> a.value))
}
