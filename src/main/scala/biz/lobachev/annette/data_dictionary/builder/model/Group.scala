package biz.lobachev.annette.data_dictionary.builder.model

case class Group(
  id: String,
  name: String,
  description: String = "",
  schema: Option[String] = None
) {
  def withSchema(schema: String) = copy(schema = Some(schema))
}
