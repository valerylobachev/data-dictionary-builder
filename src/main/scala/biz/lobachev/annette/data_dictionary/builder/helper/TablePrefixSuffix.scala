package biz.lobachev.annette.data_dictionary.builder.helper

object TablePrefixSuffix {
  val TABLE_NAME_PREFIX = "tableNamePrefix"
  val TABLE_NAME_SUFFIX = "tableNameSuffix"

  def tableNamePrefix(prefix: String) = TABLE_NAME_PREFIX -> prefix
  def tableNameSuffix(suffix: String) = TABLE_NAME_SUFFIX -> suffix

}
