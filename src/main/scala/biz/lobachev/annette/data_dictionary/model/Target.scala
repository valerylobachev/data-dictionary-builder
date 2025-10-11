package biz.lobachev.annette.data_dictionary.model

sealed trait Target

case object ScalaTarget      extends Target {
  override def toString: String = "Scala"
}
case object GoTarget         extends Target {
  override def toString: String = "Go"
}
case object TypescriptTarget extends Target {
  override def toString: String = "Typescript"
}
case object KotlinTarget     extends Target {
  override def toString: String = "Kotlin"
}
case object PostgresTarget   extends Target {
  override def toString: String = "PostgreSQL"
}
case object ClickHouseTarget extends Target {
  override def toString: String = "ClickHouse"
}
