package biz.lobachev.annette.data_dictionary.builder.rendering.markdown

case class MdDomain(
  name: String,
  description: String,
  groups: Seq[MDGroup]
)

case class MDGroup(
  name: String,
  description: String,
  entities: Seq[MdEntity]
)

case class MdEntity(
  name: String,
  description: String,
  fullTableName: String,
  fields: Seq[MdField],
  indexes: Seq[MdIndex],
  relations: Seq[MdRelation]
)

case class MdField(
  dbFieldName: String,
  description: String,
  datatype: String,
  pk: String,
  required: String
)

case class MdIndex(
  fields: String,
  unique: String,
  description: String
)

case class MdRelation(
  f1: String,
  relationEntityName: String,
  relationEntityFullTableName: String,
  f2: String,
  relationType: String,
  description: String
)
