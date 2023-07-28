package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.{EntityRelation, ManyToOne, OneToOne, Restrict}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._

trait Relations {

  def manyToOneRelation(
    id: String,
    name: String,
    referenceEntityId: String,
    fields: (String, String)*
  ) =
    EntityRelation(
      id = id.snakeCase,
      name = name,
      relationType = ManyToOne,
      referenceEntityId = referenceEntityId,
      onUpdate = Restrict,
      onDelete = Restrict,
      fields = fields
    )

  def oneToOneRelation(
    id: String,
    name: String,
    referenceEntityId: String,
    fields: (String, String)*
  ) =
    EntityRelation(
      id = id.snakeCase,
      name = name,
      relationType = OneToOne,
      referenceEntityId = referenceEntityId,
      onUpdate = Restrict,
      onDelete = Restrict,
      fields = fields
    )

}
