package biz.lobachev.annette.data_dictionary.labels

object Postgres {
  val POSTGRES_SCHEMA = "postgresSchema"
  val POSTGRES_DATA_TYPE  = "postgresDataType"
  

  def postgresSchema(schema: String) = POSTGRES_SCHEMA -> schema
  def postgresDataType(datatype: String) = POSTGRES_DATA_TYPE  -> datatype
  

}
