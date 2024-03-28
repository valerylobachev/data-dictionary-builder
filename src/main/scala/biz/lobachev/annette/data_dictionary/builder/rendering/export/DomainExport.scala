package biz.lobachev.annette.data_dictionary.builder.rendering.`export`

import biz.lobachev.annette.data_dictionary.builder.model.{DataElement, Domain, Entity, EnumData, Group, Labels}

case class DomainExport(
  id: String,
  name: String,
  description: String,
  groups: Seq[Group],
  entities: Seq[Entity],
  dataElements: Seq[DataElement],
  enums: Seq[EnumData],
  labels: Labels,
)

object DomainExport {
  def apply(domain: Domain): DomainExport =
    DomainExport(
      domain.id,
      domain.name,
      domain.description,
      domain.groups.values.toSeq,
      domain.entities.values.toSeq,
      domain.dataElements.values.toSeq,
      domain.enums.values.toSeq,
      domain.labels,
    )
}
