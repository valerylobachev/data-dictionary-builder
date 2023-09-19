package biz.lobachev.annette.data_dictionary.builder.model

case class DataElement(
  id: String,
  name: String,
  description: String = "",
  fieldName: String,
  dbFieldName: String,
  dataType: DataType,
  notNull: Boolean = true,
  attributes: Attributes = Map.empty
) {
  def withDescription(description: String) = copy(description = description)
  def withAttributes(seq: Attribute*) = copy(attributes = attributes ++ seq.map(a => a.key -> a.value))
}
