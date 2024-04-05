package biz.lobachev.annette.data_dictionary.builder.rendering.`export`

import biz.lobachev.annette.data_dictionary.builder.model.{RawComponent, RawDataElement, RawDomain, RawEntity, RawEnumData, Labels}

case class DomainExport(
                         id: String,
                         name: String,
                         description: String,
                         rootComponents: Seq[String],
                         components: Seq[RawComponent],
                         entities: Seq[RawEntity],
                         dataElements: Seq[RawDataElement],
                         enums: Seq[RawEnumData],
                         labels: Labels,
)

object DomainExport {
  def apply(domain: RawDomain): DomainExport =
    DomainExport(
      id = domain.id,
      name = domain.name,
      description = domain.description,
      rootComponents = domain.rootComponents,
      components = domain.components.values.toSeq,
      entities = domain.entities.values.toSeq,
      dataElements = domain.dataElements.values.toSeq,
      enums = domain.enums.values.toSeq,
      labels = domain.labels,
    )
}
