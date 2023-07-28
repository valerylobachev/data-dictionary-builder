package biz.lobachev.annette.data_dictionary.builder.model

case class Attribute(
  key: String,
  value: String
)

object Attribute {
  implicit def tuple2Attribute(tuple: (String, String)): Attribute = Attribute(tuple._1, tuple._2)
}
