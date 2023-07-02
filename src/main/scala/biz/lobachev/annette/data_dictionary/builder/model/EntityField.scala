package biz.lobachev.annette.data_dictionary.builder.model

case class EntityField(
  name: String,
  description: String = "",
  fieldName: String,
  dbFieldName: String,
  dataType: DataType,
  notNull: Boolean = true,
  autoIncrement: Boolean = false
) {}
