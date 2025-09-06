package biz.lobachev.annette.data_dictionary.builder.model

case class ComponentData(
  component: Component,
  entities: Seq[Entity] = Seq.empty,
  dataElements: Seq[DataElement] = Seq.empty,
  enums: Seq[EnumData] = Seq.empty,
  components: Seq[ComponentData] = Seq.empty,
) {
  def withSchema(schema: String) = copy(component = component.copy(schema = Some(schema)))

  def withEntitySeq(entitySeq: Seq[Entity]): ComponentData =
    copy(entities =
      entities ++ entitySeq.map(e =>
        e.copy(
          componentId = component.id,
          schema = e.schema.map(Some(_)).getOrElse(component.schema),
        ),
      ),
    )

  def withEntities(entitySeq: Entity*): ComponentData = withEntitySeq(entitySeq)

  def withDataElementSeq(dataElementSeq: Seq[DataElement]): ComponentData =
    copy(dataElements = dataElements ++ dataElementSeq.map(e => e.copy(componentId = Some(component.id))))

  def withDataElements(dataElementSeq: DataElement*): ComponentData = withDataElementSeq(dataElementSeq)

  def withEnumSeq(enumSeq: Seq[EnumData]): ComponentData =
    copy(enums =
      enums ++ enumSeq.map(e =>
        e.copy(
          componentId = Some(component.id),
        ),
      ),
    )

  def withEnums(enumSeq: EnumData*): ComponentData = withEnumSeq(enumSeq)

  def withComponentSeq(componentSeq: Seq[ComponentData]): ComponentData = {
    val newChildren = componentSeq.map(_.component.id)
    copy(
      component = component.copy(children = component.children ++ newChildren),
      components =
        components ++ componentSeq.map(e => e.copy(component = e.component.copy(parent = Some(component.id)))),
    )
  }

  def withComponents(componentSeq: ComponentData*): ComponentData = withComponentSeq(componentSeq)

  def withLabelSeq(seq: Seq[Label]) =
    copy(component = component.copy(labels = component.labels ++ seq.map(a => a.key -> a.value)))

  def withLabels(seq: Label*) = withLabelSeq(seq)

  @deprecated("use withLabels", since = "0.4.0")
  def withAttributes(seq: Label*) = withLabelSeq(seq)

  def expandComponents(): Seq[Component] = Seq(component) ++ components.flatMap(_.expandComponents())

  def expandEntities(): Seq[Entity] = entities ++ components.flatMap(_.expandEntities())

  def expandDataElements(): Seq[DataElement] = dataElements ++ components.flatMap(_.expandDataElements())

  def expandEnums(): Seq[EnumData] = enums ++ components.flatMap(_.expandEnums())
}
