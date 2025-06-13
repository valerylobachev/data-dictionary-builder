package biz.lobachev.annette.data_dictionary.builder.labels

object ClickHouse {
  val CLICKHOUSE_ENGINE         = "clickHouseEngine"
  val CLICKHOUSE_ENGINE_EXCLUDE = "none"

  def clickHouseEngine(engine: String) = CLICKHOUSE_ENGINE -> engine
  def clickHouseExclude()              = CLICKHOUSE_ENGINE -> CLICKHOUSE_ENGINE_EXCLUDE
}
