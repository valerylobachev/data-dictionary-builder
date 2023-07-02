package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.Group

trait Groups {

  def group(id: String, name: String, description: String = ""): GroupEntities =
    GroupEntities(
      group = Group(
        id = id,
        name = name,
        description = description
      )
    )

}
