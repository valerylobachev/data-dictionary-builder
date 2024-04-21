package biz.lobachev.annette.data_dictionary.builder.model

case class EntityIndex(
  id: String,
  name: String,
  description: String = "",
  unique: Boolean = false,
  fields: Seq[String],
  labels: Labels = Map.empty,
) {
  def withDescription(description: String) = copy(description = description)

  def withLabels(seq: Label*) = copy(labels = labels ++ seq.map(a => a.key -> a.value))
}
