package biz.lobachev.annette.data_dictionary.model

case class EnumData(
  id: String,
  name: String,
  description: String = "",
  componentId: Option[String] = None,
  labels: Labels = Map.empty,
  enumType: EnumType,
  strict: Boolean = false,
  length: Int = 0,
  elements: Seq[EnumElement],
) 

case class EnumElement(
  id: String,
  constName: String,
  name: String,
)

sealed trait EnumType
case object NativeEnum extends EnumType
case object StringEnum extends EnumType
case object IntEnum    extends EnumType
