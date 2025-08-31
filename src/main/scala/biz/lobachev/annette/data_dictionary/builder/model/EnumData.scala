package biz.lobachev.annette.data_dictionary.builder.model

case class EnumData(
  id: String,
  name: String,
  description: String = "",
  enumType: EnumType,
  strict: Boolean = false,
  length: Int = 0,
  elements: Seq[EnumElement],
  labels: Labels = Map.empty,
  componentId: Option[String] = None,
) {
  def withValues(seq: ((String, String), String)*) =
    copy(elements = seq.map { case key -> constName -> name =>
      if enumType == IntEnum && key.toIntOption == None then
        throw Exception(s"Enum $id is IntEnum, but has non integer value '$key' -  $constName - $name")
      EnumElement(key, constName, name)
    })

  def withLabels(seq: Label*) = copy(labels = labels ++ seq.map(a => a.key -> a.value))
}

case class EnumElement(
  id: String,
  constName: String,
  name: String,
)

sealed trait EnumType
case object NativeEnum extends EnumType
case object StringEnum extends EnumType
case object IntEnum    extends EnumType
