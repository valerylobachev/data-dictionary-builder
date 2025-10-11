package biz.lobachev.annette.data_dictionary.labels

object Go {
  val GO_TABLE_PACKAGE      = "goTablePackage"
  val GO_STRUCT_PACKAGE     = "goStructPackage"
  val GO_STRUCT_SOURCE_FILE = "goStructSourceFile"
  val GO_ENUM_PACKAGE       = "goEnumPackage"
  val GO_DATA_TYPE          = "goDataType"

  def goTablePackage(pkg: String)          = GO_TABLE_PACKAGE      -> pkg
  def goStructPackage(pkg: String)         = GO_STRUCT_PACKAGE     -> pkg
  def goStructSourceFile(filename: String) = GO_STRUCT_SOURCE_FILE -> filename
  def goEnumPackage(pkg: String)           = GO_ENUM_PACKAGE       -> pkg
  def goDataType(datatype: String)         = GO_DATA_TYPE          -> datatype

}
