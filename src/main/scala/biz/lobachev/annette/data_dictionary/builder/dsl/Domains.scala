package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.Domain

trait Domains {

  /** Defines domain model
    *
    * @param id
    *   unique id of model
    * @param name
    *   domain's name. Name should be single line.
    * @param description
    *   description of domain model. Description can be multiline.
    * @return
    *   domain model
    */
  def domain(id: String, name: String, description: String = ""): Domain =
    Domain(
      id = id.trim,
      name = name.trim,
      description = description.trim,
    )

}
