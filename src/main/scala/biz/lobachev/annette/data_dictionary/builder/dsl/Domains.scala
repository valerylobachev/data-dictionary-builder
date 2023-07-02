package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.{DataElement, Domain, EnumData}

import scala.collection.immutable.ListMap

trait Domains {

  def domain(id: String, name: String, description: String = ""): Domain =
    Domain(
      id = id,
      name = name,
      description = description
    )

  implicit class DomainImplicit(domain: Domain) {

    def withGroupSeq(groupSeq: Seq[GroupEntities]): Domain =
      domain.copy(
        groups = domain.groups ++ groupSeq.map(m => m.group.id -> m.group),
        entities =
          domain.entities ++ groupSeq.flatMap(g => g.entities.map(e => e.id -> e.copy(schema = g.group.schema)))
      )

    def withGroups(groupSeq: GroupEntities*) = withGroupSeq(groupSeq)

    def withDataElementSeq(seq: Seq[DataElement]) =
      domain.copy(dataElements = ListMap.from(seq.map(e => e.id -> e)))

    def withDataElements(seq: DataElement*)       = withDataElementSeq(seq)

    def withEnumSeq(seq: Seq[EnumData]) =
      domain.copy(enums = ListMap.from(seq.map(e => e.id -> e)))

    def withEnums(seq: EnumData*)       = withEnumSeq(seq)
  }
}
