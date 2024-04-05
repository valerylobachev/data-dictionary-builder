package biz.lobachev.annette.data_dictionary.builder.model

case class RawDataElement(
  id: String,
  name: String,
  description: String = "",
  fieldName: String,
  dbFieldName: String,
  dataType: DataType,
  notNull: Boolean = true,
  labels: Labels = Map.empty,
  componentId: Option[String] = None,
) {
  def withDescription(description: String) = copy(description = description)
  def withLabels(seq: Label*)              = copy(labels = labels ++ seq.map(a => a.key -> a.value))
}
