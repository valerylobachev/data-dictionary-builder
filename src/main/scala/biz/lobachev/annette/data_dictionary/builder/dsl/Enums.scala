package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.{EnumData, IntEnum, NativeEnum, StringEnum}

trait Enums {

  def nativeEnum(
    id: String,
    name: String,
    length: Int,
    description: String = "",
  ) =
    EnumData(
      id = id.trim,
      name = name.trim,
      description = description.trim,
      enumType = NativeEnum,
      strict = true,
      length = length,
      elements = Seq.empty,
    )

  def stringEnum(
    id: String,
    name: String,
    length: Int,
    description: String = "",
  ) =
    EnumData(
      id = id.trim,
      name = name.trim,
      description = description.trim,
      enumType = StringEnum,
      strict = false,
      length = length,
      elements = Seq.empty,
    )

  def strictStringEnum(
    id: String,
    name: String,
    length: Int,
    description: String = "",
  ) = EnumData(
    id = id.trim,
    name = name.trim,
    description = description.trim,
    enumType = StringEnum,
    strict = true,
    length = length,
    elements = Seq.empty,
  )

  def intEnum(
    id: String,
    name: String,
    description: String = "",
  ) =
    EnumData(
      id = id.trim,
      name = name.trim,
      description = description.trim,
      enumType = IntEnum,
      elements = Seq.empty,
    )

  def strictIntEnum(
    id: String,
    name: String,
    description: String = "",
  ) =
    EnumData(
      id = id.trim,
      name = name.trim,
      description = description.trim,
      enumType = IntEnum,
      strict = true,
      elements = Seq.empty,
    )

}
