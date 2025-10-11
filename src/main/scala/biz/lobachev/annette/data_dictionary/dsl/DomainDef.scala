package biz.lobachev.annette.data_dictionary.dsl

case class DomainDef(
  id: String,
  name: String,
  description: String = "",
  labels: Seq[Label] = Seq.empty,
  includes: Seq[IncludeDef] = Seq.empty,
  enums: Seq[EnumDataDef] = Seq.empty,
  dataElements: Seq[DataElementDef] = Seq.empty,
  tables: Seq[TableDef] = Seq.empty,
  structs: Seq[StructDef] = Seq.empty,
  components: Seq[ComponentDef] = Seq.empty,
) {
  def withDescription(description: String)                = copy(description = description)
  def withLabels(seq: Label*)                             = copy(labels = labels ++ seq)
  def withEnums(seq: EnumDataDef*)                        = copy(enums = enums ++ seq)
  def withDataElements(seq: DataElementDef*)              = copy(dataElements = dataElements ++ seq)
  def withTables(seq: TableDef*)                          = copy(tables = tables ++ seq)
  def withStructs(seq: StructDef*)                        = copy(structs = structs ++ seq)
  def withLabeledStructs(labels: Label*)(seq: StructDef*) =
    copy(
      structs = structs ++
        seq.map { dm =>
          dm.copy(labels = dm.labels ++ labels)
        },
    )
  def withIncludes(seq: IncludeDef*)                      = copy(includes = includes ++ seq)
  def withComponents(seq: ComponentDef*)                  = copy(components = components ++ seq)
}
