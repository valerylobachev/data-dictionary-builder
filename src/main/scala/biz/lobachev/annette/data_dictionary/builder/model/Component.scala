package biz.lobachev.annette.data_dictionary.builder.model

case class Component(
  id: String,
  name: String,
  description: String = "",
  parent: Option[String] = None,
  children: Seq[String] = Seq.empty,
  schema: Option[String] = None,
  labels: Labels = Map.empty,
) {
  def withSchema(schema: String) = copy(schema = Some(schema))

  def withLabels(seq: Label*) = copy(labels = labels ++ seq.map(a => a.key -> a.value))
}
