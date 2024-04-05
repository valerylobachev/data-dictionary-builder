package biz.lobachev.annette.data_dictionary.builder.model

case class RawComponentData(
  component: RawComponent,
  entities: Seq[RawEntity] = Seq.empty,
  dataElements: Seq[RawDataElement] = Seq.empty,
  components: Seq[RawComponentData] = Seq.empty,
) {
  def withSchema(schema: String) = copy(component = component.copy(schema = Some(schema)))

  def withEntitySeq(entitySeq: Seq[RawEntity]): RawComponentData =
    copy(entities =
      entities ++ entitySeq.map(e =>
        e.copy(
          componentId = component.id,
          schema = e.schema.map(Some(_)).getOrElse(component.schema),
        ),
      ),
    )

  def withEntities(entitySeq: RawEntity*): RawComponentData = withEntitySeq(entitySeq)

  def withDataElementSeq(dataElementSeq: Seq[RawDataElement]): RawComponentData =
    copy(dataElements = dataElements ++ dataElementSeq.map(e => e.copy(componentId = Some(component.id))))

  def withDataElements(dataElementSeq: RawDataElement*): RawComponentData = withDataElementSeq(dataElementSeq)

  def withComponentSeq(componentSeq: Seq[RawComponentData]): RawComponentData = {
    val newChildren = componentSeq.map(_.component.id)
    copy(
      component = component.copy(children = component.children ++ newChildren),
      components =
        components ++ componentSeq.map(e => e.copy(component = e.component.copy(parent = Some(component.id)))),
    )
  }

  def withComponents(componentSeq: RawComponentData*): RawComponentData = withComponentSeq(componentSeq)

  def withLabels(seq: Label*) =
    copy(component = component.copy(labels = component.labels ++ seq.map(a => a.key -> a.value)))

  def expandComponents(): Seq[RawComponent] = Seq(component) ++ components.flatMap(_.expandComponents())

  def expandEntities(): Seq[RawEntity] = entities ++ components.flatMap(_.expandEntities())

  def expandDataElements(): Seq[RawDataElement] = dataElements ++ components.flatMap(_.expandDataElements())

}
