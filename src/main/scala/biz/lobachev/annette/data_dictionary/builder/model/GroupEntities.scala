package biz.lobachev.annette.data_dictionary.builder.model

case class GroupEntities(
  group: Group,
  entities: Seq[Entity] = Seq.empty
) {
  def withSchema(schema: String) = copy(group = group.copy(schema = Some(schema)))

  def withEntitySeq(entitySeq: Seq[Entity]): GroupEntities =
    copy(entities = entities ++ entitySeq.map(e => e.copy(groupId = group.id)))

  def withEntities(entitySeq: Entity*): GroupEntities = withEntitySeq(entitySeq)

  def withAttributes(seq: Attribute*) = copy(group = group.copy(attributes = group.attributes ++ seq))

}
