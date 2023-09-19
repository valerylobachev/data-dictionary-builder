package biz.lobachev.annette.data_dictionary.builder.model

case class EnumData(
  id: String,
  name: String,
  description: String = "",
  length: Int,
  elements: Seq[(String, String)],
  attributes: Attributes = Map.empty
) {
  def withValues(seq: (String, String)*) = copy(elements = seq)

  def withAttributes(seq: Attribute*) = copy(attributes = attributes ++ seq.map(a => a.key -> a.value))
}
