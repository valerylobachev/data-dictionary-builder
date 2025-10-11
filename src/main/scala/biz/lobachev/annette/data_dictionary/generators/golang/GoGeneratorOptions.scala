package biz.lobachev.annette.data_dictionary.generators.golang

case class GoGeneratorOptions(
  dbLib: DbLib,
)

sealed trait DbLib
case class Gorm(
  fieldNullable: Boolean = true,
) extends DbLib
case object Sqlx extends DbLib
