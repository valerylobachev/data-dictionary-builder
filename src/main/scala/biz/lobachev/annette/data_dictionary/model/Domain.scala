package biz.lobachev.annette.data_dictionary.model

import scala.collection.immutable.ListMap

case class Domain(
  id: String,
  name: String,
  description: String = "",
  labels: Labels = Map.empty,
  rootComponentIds: Seq[String] = Seq.empty,
  components: ListMap[String, Component] = ListMap.empty,
  enums: ListMap[String, EnumData] = ListMap.empty,
  dataElements: ListMap[String, DataElement] = ListMap.empty,
  tables: ListMap[String, Table] = ListMap.empty,
  structs: ListMap[String, Struct] = ListMap.empty,
)
