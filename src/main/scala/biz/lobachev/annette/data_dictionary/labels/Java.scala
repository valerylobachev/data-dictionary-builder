package biz.lobachev.annette.data_dictionary.labels

object Java {
  val JAVA_TABLE_PACKAGE      = "javaTablePackage"
  val JAVA_STRUCT_PACKAGE = "javaStructPackage"
  val JAVA_ENUM_PACKAGE       = "javaEnumPackage"
  val KOTLIN_DATA_TYPE        = "kotlinDataType"

  def javaStructPackage(pkg: String) = JAVA_STRUCT_PACKAGE -> pkg
  def javaTablePackage(pkg: String)     = JAVA_TABLE_PACKAGE      -> pkg
  def javaEnumPackage(pkg: String)      = JAVA_ENUM_PACKAGE       -> pkg
  def kotlinDataType(datatype: String)  = KOTLIN_DATA_TYPE        -> datatype

}
