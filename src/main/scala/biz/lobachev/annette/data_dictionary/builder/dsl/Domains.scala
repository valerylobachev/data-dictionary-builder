package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.Domain

trait Domains {

  def domain(id: String, name: String, description: String = ""): Domain =
    Domain(
      id = id,
      name = name,
      description = description
    )

}
