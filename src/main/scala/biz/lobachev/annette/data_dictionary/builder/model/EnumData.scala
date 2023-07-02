package biz.lobachev.annette.data_dictionary.builder.model

case class EnumData(
  id: String,
  name: String,
  description: String = "",
  length: Int,
  elements: Seq[(String, String)]
)
