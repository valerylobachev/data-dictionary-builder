package biz.lobachev.annette.data_dictionary.dsl

case class ComponentDef(
  id: String,
  name: String,
  description: String = "",
  labels: Seq[Label] = Seq.empty,
  enums: Seq[EnumDataDef] = Seq.empty,
  dataElements: Seq[DataElementDef] = Seq.empty,
  tables: Seq[TableDef] = Seq.empty,
  structs: Seq[StructDef] = Seq.empty,
  includes: Seq[IncludeDef] = Seq.empty,
  components: Seq[ComponentDef] = Seq.empty,
  componentId: Option[String] = None,
) {
  def withDescription(description: String)                = copy(description = description)
  def withLabels(seq: Label*)                             = copy(labels = labels ++ seq)
  def withEnums(seq: EnumDataDef*)                        = copy(enums = enums ++ seq.map(_.copy(componentId = Some(id))))
  def withDataElements(seq: DataElementDef*)              =
    copy(dataElements = dataElements ++ seq.map(_.copy(componentId = Some(id))))
  def withTables(seq: TableDef*)                          = copy(tables = tables ++ seq.map(_.copy(componentId = Some(id))))
  def withStructs(seq: StructDef*)                        = copy(structs = structs ++ seq.map(_.copy(componentId = Some(id))))
  def withLabeledStructs(labels: Label*)(seq: StructDef*) =
    copy(
      structs = structs ++
        seq.map { dm =>
          dm.copy(labels = dm.labels ++ labels)
        },
    )
  def withIncludes(seq: IncludeDef*)                      = copy(includes = includes ++ seq.map(_.copy(componentId = Some(id))))
  def withComponents(seq: ComponentDef*)                  = copy(components = components ++ seq.map(_.copy(componentId = Some(id))))
}
