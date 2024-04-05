package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.RawDomain

trait Domains {

  def domain(id: String, name: String, description: String = ""): RawDomain =
    RawDomain(
      id = id.trim,
      name = name.trim,
      description = description.trim
    )

}
