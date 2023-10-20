package biz.lobachev.annette.data_dictionary.builder.rendering.json

import biz.lobachev.annette.data_dictionary.builder.model.{Attributes, DataElement, Domain, Entity, EnumData, Group}

case class DomainExport(
  id: String,
  name: String,
  description: String,
  groups: Seq[Group],
  entities: Seq[Entity],
  dataElements: Seq[DataElement],
  enums: Seq[EnumData],
  attributes: Attributes,
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
      domain.attributes,
    )
}
