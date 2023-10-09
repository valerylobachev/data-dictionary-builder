package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.{Group, GroupEntities}

trait Groups {

  def group(id: String, name: String, description: String = ""): GroupEntities =
    GroupEntities(
      group = Group(
        id = id.trim,
        name = name.trim,
        description = description.trim
      )
    )

}
