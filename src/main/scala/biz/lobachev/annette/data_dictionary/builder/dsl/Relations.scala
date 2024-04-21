package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.{EntityRelation, ManyToOne, NoAction, OneToOne}

trait Relations {

  def manyToOneRelation(
    id: String,
    name: String,
    referenceEntityId: String,
    fields: (String, String)*,
  ) =
    EntityRelation(
      id = id.trim,
      name = name.trim,
      relationType = ManyToOne,
      referenceEntityId = referenceEntityId.trim,
      onUpdate = NoAction,
      onDelete = NoAction,
      fields = fields,
    )

  def oneToOneRelation(
    id: String,
    name: String,
    referenceEntityId: String,
    fields: (String, String)*,
  ) =
    EntityRelation(
      id = id.trim,
      name = name.trim,
      relationType = OneToOne,
      referenceEntityId = referenceEntityId.trim,
      onUpdate = NoAction,
      onDelete = NoAction,
      fields = fields,
    )

}
