package biz.lobachev.annette.data_dictionary.builder.labels

object RelationName {
  val RELATION_FIELD_NAME     = "relationFieldName"
  val RELATION_REL_FIELD_NAME = "relationRelFieldName"

  def fieldName(name: String)    = RELATION_FIELD_NAME     -> name
  def relFieldName(name: String) = RELATION_REL_FIELD_NAME -> name
}
