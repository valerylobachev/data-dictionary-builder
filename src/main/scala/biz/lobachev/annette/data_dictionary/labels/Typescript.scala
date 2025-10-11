package biz.lobachev.annette.data_dictionary.labels

object Typescript {
  val TS_STRUCT_PACKAGE = "tsStructPackage"
  val TS_ENUM_PACKAGE   = "tsEnumPackage"

  def tsStructPackage(pkg: String) = TS_STRUCT_PACKAGE -> pkg
  def tsEnumPackage(pkg: String)   = TS_ENUM_PACKAGE   -> pkg
}
