package biz.lobachev.annette.data_dictionary.model

case class Component(
  id: String,
  name: String,
  description: String = "",
  parent: Option[String] = None,
  children: Seq[String] = Seq.empty,
  labels: Labels = Map.empty,
) 
