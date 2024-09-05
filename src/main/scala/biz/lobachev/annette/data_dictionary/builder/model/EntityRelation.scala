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
  logical: Boolean = false,
  association: Option[Association] = None,
) {
  def withFields(seq: (String, String)*) = copy(fields = seq)

  def withLabels(seq: Label*) = copy(labels = labels ++ seq.map(a => a.key -> a.value))

  def withOnUpdate(act: ForeignKeyAction) = copy(onUpdate = act)
  def withOnDelete(act: ForeignKeyAction) = copy(onDelete = act)

  def withCascadeDelete()      = copy(onDelete = Cascade)
  def withRestrictDelete()     = copy(onDelete = Restrict)
  def withSetNullOnDelete()    = copy(onDelete = SetNull)
  def withSetDefaultOnDelete() = copy(onDelete = SetDefault)

  def withAssociation(name: String)            = copy(association = Some(Association(name)))
  def withAssociation(assoc: (String, String)) = copy(association = Some(Association(assoc._1, Some(assoc._2))))
}

case class Association(
  name: String,
  referenceName: Option[String] = None,
)
