package biz.lobachev.annette.data_dictionary.builder.model

case class EntityField(
  name: String,
  description: String = "",
  fieldName: String,
  dbFieldName: String,
  dataType: DataType,
  notNull: Boolean = true,
  autoIncrement: Boolean = false,
  labels: Labels = Map.empty,
) {
  def withLabels(seq: Label*) = copy(labels = labels ++ seq.map(a => a.key -> a.value))
}
