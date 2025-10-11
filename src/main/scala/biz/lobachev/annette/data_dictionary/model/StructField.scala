package biz.lobachev.annette.data_dictionary.model

case class StructField(
  fieldName: String,
  name: String,
  description: String = "",
  labels: Labels = Map.empty,
  datatype: Datatype,
  optional: Boolean = false,
)
