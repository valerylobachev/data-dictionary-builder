package biz.lobachev.annette.data_dictionary.builder.model

case class RawEntityRelation(
  id: String,
  name: String,
  description: String = "",
  relationType: RelationType,
  referenceEntityId: String,
  fields: Seq[(String, String)],
  onUpdate: ForeignKeyAction = NoAction,
  onDelete: ForeignKeyAction = NoAction,
  labels: Labels = Map.empty,
) {
  def withFields(seq: (String, String)*) = copy(fields = seq)

  def withLabels(seq: Label*) = copy(labels = labels ++ seq.map(a => a.key -> a.value))
}


