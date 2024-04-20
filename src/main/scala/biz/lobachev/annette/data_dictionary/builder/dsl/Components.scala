package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.{Component, ComponentData}

trait Components {

  def component(id: String, name: String, description: String = ""): ComponentData =
    ComponentData(
      component = Component(
        id = id.trim,
        name = name.trim,
        description = description.trim,
      ),
    )

  def group(id: String, name: String, description: String = ""): ComponentData =
    component(id, name, description)

}
