package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.RawEntityIndex
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._

trait Indexes {
  def index(id: String, name: String, fields: String*) =
    RawEntityIndex(
      id = id.trim.snakeCase,
      name = name.trim,
      fields = fields
    )

  def uniqueIndex(id: String, name: String, fields: String*) =
    RawEntityIndex(
      id = id.trim.snakeCase,
      name = name.trim,
      unique = true,
      fields = fields
    )

}
