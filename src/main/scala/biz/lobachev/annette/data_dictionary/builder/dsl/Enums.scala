package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.EnumData

trait Enums {

  def enumDef(
    id: String,
    name: String,
    length: Int,
    description: String = ""
  ) =
    EnumData(
      id = id.trim,
      name = name.trim,
      description = description.trim,
      length = length,
      elements = Seq.empty
    )

}
