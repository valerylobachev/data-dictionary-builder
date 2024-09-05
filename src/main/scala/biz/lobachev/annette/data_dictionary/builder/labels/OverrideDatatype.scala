package biz.lobachev.annette.data_dictionary.builder.labels

object OverrideDatatype {
  val KOTLIN_DATA_TYPE = "kotlinDataType"
  val GO_DATA_TYPE = "goDataType"
  val POSTGRESQL_DATA_TYPE = "postgresDataType"

  def kotlinDataType(datatype: String) = KOTLIN_DATA_TYPE -> datatype
  def goDataType(datatype: String) = GO_DATA_TYPE -> datatype
  def postgreSqlDataType(datatype: String) = POSTGRESQL_DATA_TYPE -> datatype
}
