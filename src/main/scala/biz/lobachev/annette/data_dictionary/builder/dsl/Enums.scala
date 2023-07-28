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
      id = id,
      name = name,
      description = description,
      length = length,
      elements = Seq.empty
    )

}
