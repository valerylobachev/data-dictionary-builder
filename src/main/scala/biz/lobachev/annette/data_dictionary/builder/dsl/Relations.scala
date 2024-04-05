package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.{RawEntityRelation, ManyToOne, OneToOne, Restrict}

trait Relations {

  def manyToOneRelation(
    id: String,
    name: String,
    referenceEntityId: String,
    fields: (String, String)*,
  ) =
    RawEntityRelation(
      id = id.trim,
      name = name.trim,
      relationType = ManyToOne,
      referenceEntityId = referenceEntityId.trim,
      onUpdate = Restrict,
      onDelete = Restrict,
      fields = fields,
    )

  def oneToOneRelation(
    id: String,
    name: String,
    referenceEntityId: String,
    fields: (String, String)*,
  ) =
    RawEntityRelation(
      id = id.trim,
      name = name.trim,
      relationType = OneToOne,
      referenceEntityId = referenceEntityId.trim,
      onUpdate = Restrict,
      onDelete = Restrict,
      fields = fields,
    )

}
