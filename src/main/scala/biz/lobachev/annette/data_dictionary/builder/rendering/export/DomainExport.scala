package biz.lobachev.annette.data_dictionary.builder.rendering.`export`

import biz.lobachev.annette.data_dictionary.builder.model.{Component, DataElement, Domain, Entity, EnumData, Labels}

case class DomainExport(
  id: String,
  name: String,
  description: String,
  rootComponents: Seq[String],
  components: Seq[Component],
  entities: Seq[Entity],
  dataElements: Seq[DataElement],
  enums: Seq[EnumData],
  labels: Labels,
)

object DomainExport {
  def apply(domain: Domain): DomainExport =
    DomainExport(
      id = domain.id,
      name = domain.name,
      description = domain.description,
      rootComponents = domain.rootComponentIds,
      components = domain.components.values.toSeq,
      entities = domain.entities.values.toSeq,
      dataElements = domain.dataElements.values.toSeq,
      enums = domain.enums.values.toSeq,
      labels = domain.labels,
    )
}
