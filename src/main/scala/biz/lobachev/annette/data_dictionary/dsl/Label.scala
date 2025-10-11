package biz.lobachev.annette.data_dictionary.dsl

case class Label(
  key: String,
  value: String,
)

object Label {
  implicit def tupleToLabel(tuple: (String, String)): Label = Label(tuple._1, tuple._2)
}
