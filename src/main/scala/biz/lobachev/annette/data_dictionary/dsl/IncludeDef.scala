package biz.lobachev.annette.data_dictionary.dsl

case class IncludeDef(
  id: String,
  name: String,
  description: String = "",
  fields: Seq[TableFieldDef] = Seq.empty,
  componentId: Option[String] = None,
) {
  def withDescription(description: String) = copy(description = description)
  def withFields(seq: TableFieldDef*)      = copy(fields = fields ++ seq)
}
