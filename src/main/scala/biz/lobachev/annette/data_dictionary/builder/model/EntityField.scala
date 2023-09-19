package biz.lobachev.annette.data_dictionary.builder.model

case class EntityField(
  name: String,
  description: String = "",
  fieldName: String,
  dbFieldName: String,
  dataType: DataType,
  notNull: Boolean = true,
  autoIncrement: Boolean = false,
  attributes: Attributes = Map.empty
) {
  def withAttributes(seq: Attribute*) = copy(attributes = attributes ++ seq.map(a => a.key -> a.value))
}
