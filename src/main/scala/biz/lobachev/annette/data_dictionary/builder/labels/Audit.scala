package biz.lobachev.annette.data_dictionary.builder.labels

object Audit {
  val AUDIT_TABLE  = "auditTable"
  val DISABLE_AUDIT = "disableAudit"

  def audit(table: String) = AUDIT_TABLE  -> table
  def disableAudit()             = DISABLE_AUDIT -> "true"
}
