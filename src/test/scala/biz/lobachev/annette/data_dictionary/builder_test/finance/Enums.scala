package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._

trait Enums {

  val enums = Seq(
    enumDef("LedgerType", "Ledger Type", 2)
      .withValues(
        "L" -> "Leading ledger",
        "X" -> "Extension ledger"
      ),
    enumDef("GLAccountType", "General ledger account type", 1)
      .withValues(
        "X" -> "Balance Sheet Account",
        "N" -> "Nonoperating Expense or Income",
        "P" -> "Primary Costs or Revenue",
        "S" -> "Secondary Costs",
        "C" -> "Cash Account"
      ),
    enumDef("AccountType", "Account type", 1)
      .withValues(
        "A" -> "Assets",
        "D" -> "Customer",
        "K" -> "Vendor",
        "M" -> "Materials",
        "S" -> "General Ledger"
      ),
    enumDef("DebitCredit", "Debit/Credit", 1)
      .withValues(
        "D" -> "Debit",
        "C" -> "Credit"
      )
  )

}
