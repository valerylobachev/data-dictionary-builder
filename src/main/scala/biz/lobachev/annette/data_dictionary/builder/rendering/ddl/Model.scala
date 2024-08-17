package biz.lobachev.annette.data_dictionary.builder.rendering.ddl

case class DDLSegments(
  tables: String,
  indexes: String,
  comments: String,
  relations: String,
  audit: Audit,
)

case class Audit(
  auditTables: Set[String],
  generatedAudit: Seq[String],
)

object Audit {
  val empty = Audit(Set.empty, Seq.empty)

  def aggregate(a: Iterable[Audit]): Audit = a
    .fold(Audit.empty) { (a, b) =>
      Audit(
        a.auditTables ++ b.auditTables,
        a.generatedAudit ++ b.generatedAudit,
      )
    }
}
