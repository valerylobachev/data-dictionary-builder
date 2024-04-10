package biz.lobachev.annette.data_dictionary.builder.model

case class RawEnumData(
  id: String,
  name: String,
  description: String = "",
  length: Int,
  elements: Seq[(String, String)],
  labels: Labels = Map.empty,
  componentId: Option[String] = None,
) {
  def withValues(seq: (String, String)*) = copy(elements = seq)

  def withLabels(seq: Label*) = copy(labels = labels ++ seq.map(a => a.key -> a.value))
}
