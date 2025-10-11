package biz.lobachev.annette.data_dictionary.model

case class TableIndex(
  id: String,
  name: String,
  description: String = "",
  labels: Labels = Map.empty,
  unique: Boolean = false,
  fields: Seq[IndexField],
  method: Option[IndexMethod] = None,
  condition: Option[String] = None,
  includeFields: Seq[String] = Seq.empty,
)

case class IndexField(
  fieldName: String,
  descending: Boolean = false,
  indexNulls: Option[IndexNulls] = None,
) {
  def nullsFirst = this.copy(indexNulls = Some(NullsFirst))
  def nullsLast = this.copy(indexNulls = Some(NullsLast))
}

object IndexField {
  given Conversion[String, IndexField] with
    def apply(fieldName: String): IndexField = IndexField(fieldName)
}

sealed trait IndexNulls
case object NullsFirst extends IndexNulls
case object NullsLast  extends IndexNulls

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
