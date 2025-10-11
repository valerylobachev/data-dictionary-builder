package biz.lobachev.annette.data_dictionary.model

case class DataElement(
  id: String,
  name: String,
  description: String = "",
  componentId: Option[String] = None,
  labels: Labels = Map.empty,
  fieldName: String,
  dbFieldName: String,
  datatype: Datatype,
)
