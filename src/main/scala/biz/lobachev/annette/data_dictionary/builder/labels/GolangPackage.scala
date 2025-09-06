package biz.lobachev.annette.data_dictionary.builder.labels

object GolangPackage {
  val GO_TABLE_PACKAGE  = "goTablePackage"
  val GO_STRUCT_PACKAGE = "goStructPackage"
  val GO_ENUM_PACKAGE   = "goEnumPackage"
  val GO_REPO_PACKAGE   = "goRepoPackage"

  def goTablePackage(pkg: String)  = GO_TABLE_PACKAGE  -> pkg
  def goStructPackage(pkg: String) = GO_STRUCT_PACKAGE -> pkg
  def goEnumPackage(pkg: String)   = GO_ENUM_PACKAGE   -> pkg
  def goRepoPackage(pkg: String)   = GO_REPO_PACKAGE   -> pkg
}
