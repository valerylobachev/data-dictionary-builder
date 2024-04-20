package biz.lobachev.annette.data_dictionary.builder.helper

@deprecated("use biz.lobachev.annette.data_dictionary.builder.labels.TablePrefixSuffix", "0.4.0")
object TablePrefixSuffix {
  val TABLE_NAME_PREFIX = "tableNamePrefix"
  val TABLE_NAME_SUFFIX = "tableNameSuffix"

  def tableNamePrefix(prefix: String) = TABLE_NAME_PREFIX -> prefix
  def tableNameSuffix(suffix: String) = TABLE_NAME_SUFFIX -> suffix

}
