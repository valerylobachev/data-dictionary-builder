package biz.lobachev.annette.data_dictionary.builder.labels

object JavaPackage {
  val JAVA_MODEL_PACKAGE = "javaModelPackage"
  val JAVA_REPO_PACKAGE  = "javaRepoPackage"

  def javaModelPackage(pkg: String) = JAVA_MODEL_PACKAGE -> pkg
  def javaRepoPackage(pkg: String)  = JAVA_REPO_PACKAGE  -> pkg
}
