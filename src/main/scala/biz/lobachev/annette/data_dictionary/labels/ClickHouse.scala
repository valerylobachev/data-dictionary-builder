package biz.lobachev.annette.data_dictionary.labels

object ClickHouse {
  val CLICKHOUSE_ENGINE         = "clickHouseEngine"
  val CLICKHOUSE_ENGINE_EXCLUDE = "none"
  val CLICK_HOUSE_DATA_TYPE     = "clickHouseDataType"

  def clickHouseEngine(engine: String) = CLICKHOUSE_ENGINE -> engine
  def clickHouseExclude()              = CLICKHOUSE_ENGINE -> CLICKHOUSE_ENGINE_EXCLUDE
  def clickHouseDataType(datatype: String) = CLICK_HOUSE_DATA_TYPE -> datatype
  
}
