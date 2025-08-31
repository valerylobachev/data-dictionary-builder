package biz.lobachev.annette.data_dictionary.builder.labels

object GolangPackage {
  val GO_MODEL_PACKAGE = "goModelPackage"
  val GO_REPO_PACKAGE  = "goRepoPackage"
  val GO_ENUM_PACKAGE  = "goEnumPackage"

  def goModelPackage(pkg: String) = GO_MODEL_PACKAGE -> pkg
  def goRepoPackage(pkg: String)  = GO_REPO_PACKAGE  -> pkg
  def goEnumPackage(pkg: String)  = GO_ENUM_PACKAGE  -> pkg
}
