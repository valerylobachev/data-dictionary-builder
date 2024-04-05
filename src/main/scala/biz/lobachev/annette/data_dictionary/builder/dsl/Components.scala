package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.{RawComponent, RawComponentData}

trait Components {

  def component(id: String, name: String, description: String = ""): RawComponentData =
    RawComponentData(
      component = RawComponent(
        id = id.trim,
        name = name.trim,
        description = description.trim,
      ),
    )

  def group(id: String, name: String, description: String = ""): RawComponentData =
    component(id, name, description)

}
