package biz.lobachev.annette.data_dictionary.dsl

import biz.lobachev.annette.data_dictionary.model.{EnumElement, EnumType}

case class EnumDataDef(
  id: String,
  name: String,
  description: String = "",
  labels: Seq[Label] = Seq.empty,
  enumType: EnumType,
  strict: Boolean = false,
  length: Int = 0,
  elements: Seq[EnumElement] = Seq.empty,
  componentId: Option[String] = None,
) {
  def withDescription(description: String) = copy(description = description)

  def withLabels(seq: Label*) = copy(labels = labels ++ seq)

  def withValues(seq: ((String, String), String)*) =
    copy(elements = seq.map { case key -> constName -> name =>
      EnumElement(key, constName, name)
    })

}
