package biz.lobachev.annette.data_dictionary.dsl

case class TableDef(
  id: String,
  name: String,
  description: String = "",
  labels: Seq[Label] = Seq.empty,
  entityName: String,
  tableName: String,
  pk: Seq[TableFieldDef] = Seq.empty,
  fields: Seq[TableFieldDef] = Seq.empty,
  indexes: Seq[TableIndexDef] = Seq.empty,
  relations: Seq[TableRelationDef] = Seq.empty,
  componentId: Option[String] = None,
) {
  def withDescription(description: String)  = copy(description = description)
  def withLabels(seq: Label*)               = copy(labels = labels ++ seq)
  def withTableName(tableName: String)      = copy(tableName = tableName)
  def withPK(seq: TableFieldDef*)           = copy(pk = pk ++ seq)
  def withFields(seq: TableFieldDef*)       = copy(fields = fields ++ seq)
  def withIndexes(seq: TableIndexDef*)      = copy(indexes = indexes ++ seq)
  def withRelations(seq: TableRelationDef*) = copy(relations = relations ++ seq)
}
