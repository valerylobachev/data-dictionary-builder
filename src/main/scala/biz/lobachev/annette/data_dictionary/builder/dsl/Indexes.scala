package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.*

trait Indexes {
  def index(id: String, name: String, fields: String*) =
    EntityIndex(
      id = id.trim,
      name = name.trim,
      fields = fields,
    )

  def uniqueIndex(id: String, name: String, fields: String*) =
    EntityIndex(
      id = id.trim,
      name = name.trim,
      unique = true,
      fields = fields,
    )

}
