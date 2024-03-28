package biz.lobachev.annette.data_dictionary.builder.model

case class GroupEntities(
  group: Group,
  entities: Seq[Entity] = Seq.empty
) {
  def withSchema(schema: String) = copy(group = group.copy(schema = Some(schema)))

  def withEntitySeq(entitySeq: Seq[Entity]): GroupEntities =
    copy(entities = entities ++ entitySeq.map(e => e.copy(groupId = group.id)))

  def withEntities(entitySeq: Entity*): GroupEntities = withEntitySeq(entitySeq)

  def withLabels(seq: Label*) = copy(group = group.copy(labels = group.labels ++ seq.map(a => a.key -> a.value)))

}
