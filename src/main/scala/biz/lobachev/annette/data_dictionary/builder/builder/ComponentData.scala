package biz.lobachev.annette.data_dictionary.builder.builder

import biz.lobachev.annette.data_dictionary.builder.model.{Component, DataElement, Entity, EnumData}

import scala.collection.immutable.Seq

case class ComponentData(
  components: Seq[Component] = Seq.empty,
  entities: Seq[Entity] = Seq.empty,
  dataElements: Seq[DataElement] = Seq.empty,
  enums: Seq[EnumData] = Seq.empty,
)
