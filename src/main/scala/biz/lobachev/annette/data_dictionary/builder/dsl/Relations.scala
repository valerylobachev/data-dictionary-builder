package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.{EntityRelation, ManyToOne, NoAction, OneToOne}

trait Relations {

  def manyToOne(
    id: String,
    name: String,
    referenceEntityId: String,
    fields: (String, String)*,
  ): EntityRelation =
    EntityRelation(
      id = id.trim,
      name = name.trim,
      relationType = ManyToOne,
      referenceEntityId = referenceEntityId.trim,
      onUpdate = NoAction,
      onDelete = NoAction,
      fields = fields,
    )

  def logicalManyToOne(
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
      logical = true,
    )

  def oneToOne(
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

  def logicalOneToOne(
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
      logical = true,
    )

  @deprecated("use oneToOne", "0.4.5")
  def oneToOneRelation(
    id: String,
    name: String,
    referenceEntityId: String,
    fields: (String, String)*,
  ) = oneToOne(id, name, referenceEntityId, fields*)

  @deprecated("use manyToOne", "0.4.5")
  def manyToOneRelation(
    id: String,
    name: String,
    referenceEntityId: String,
    fields: (String, String)*,
  ) = manyToOne(id, name, referenceEntityId, fields*)

}
