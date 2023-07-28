package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.EntityIndex
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._

trait Indexes {
  def index(id: String, name: String, fields: String*) =
    EntityIndex(
      id = id.snakeCase,
      name = name,
      fields = fields
    )

  def uniqueIndex(id: String, name: String, fields: String*) =
    EntityIndex(
      id = id.snakeCase,
      name = name,
      unique = true,
      fields = fields
    )

}
