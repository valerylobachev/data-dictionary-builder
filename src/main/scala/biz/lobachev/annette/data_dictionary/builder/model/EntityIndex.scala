package biz.lobachev.annette.data_dictionary.builder.model


case class EntityIndex(
  id: String,
  name: String,
  description: String = "",
  unique: Boolean = false,
  fields: Seq[String],
  labels: Labels = Map.empty,
  method: Option[IndexMethod] = None,
  condition: Option[String] = None,
  includeFields: Seq[String] = Seq.empty
) {
  def withDescription(description: String) = copy(description = description)

  def withLabels(seq: Label*) = copy(labels = labels ++ seq.map(a => a.key -> a.value))

  def withMethod(m: IndexMethod) = copy(method = Some(m))

  def where(c: String) = copy(condition = Some(c))

  def include(seq: String*) = copy(includeFields = includeFields ++ seq)
}

sealed trait IndexMethod {
  def method: String
}

case object BTreeMethod extends IndexMethod {
  override def method: String = "btree"
}

case object HashMethod extends IndexMethod {
  override def method: String = "hash"
}

case object GistMethod extends IndexMethod {
  override def method: String = "gist"
}

case object SpGistMethod extends IndexMethod {
  override def method: String = "spgist"
}

case object GinMethod extends IndexMethod {
  override def method: String = "gin"
}

case object BrinMethod extends IndexMethod {
  override def method: String = "brin"
}
