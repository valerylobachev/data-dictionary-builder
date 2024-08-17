package biz.lobachev.annette.data_dictionary.builder.model

case class EntityRelation(
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

  def withOnUpdate(act: ForeignKeyAction) = copy(onUpdate = act)
  def withOnDelete(act: ForeignKeyAction) = copy(onDelete = act)

  def withCascadeDelete()      = copy(onDelete = Cascade)
  def withRestrictDelete()     = copy(onDelete = Restrict)
  def withSetNullOnDelete()    = copy(onDelete = SetNull)
  def withSetDefaultOnDelete() = copy(onDelete = SetDefault)
}


