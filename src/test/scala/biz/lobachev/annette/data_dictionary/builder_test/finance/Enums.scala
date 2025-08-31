package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL.*

trait Enums {
  val x     = "" -> "" -> ""
  val enums = Seq(
    nativeEnum("LedgerType", "Ledger Type", 2)
      .withValues(
        "L" -> "LeadingLedger"               -> "Leading ledger",
        "X" -> "ExtensionLedger"             -> "Extension ledger",
      ),
    nativeEnum("GLAccountType", "General ledger account type", 1)
      .withValues(
        "X" -> "BalanceSheetAccount"         -> "Balance Sheet Account",
        "N" -> "NonOperatingExpenseOrIncome" -> "Nonoperating Expense or Income",
        "P" -> "PrimaryCostsOrRevenue"       -> "Primary Costs or Revenue",
        "S" -> "SecondaryCosts"              -> "Secondary Costs",
        "C" -> "CashAccount"                 -> "Cash Account",
      ),
    nativeEnum("AccountType", "Account type", 1)
      .withValues(
        "A" -> "Assets"                      -> "Assets",
        "D" -> "Customer"                    -> "Customer",
        "K" -> "Vendor"                      -> "Vendor",
        "M" -> "Materials"                   -> "Materials",
        "S" -> "GeneralLedger"               -> "General Ledger",
      ),
    nativeEnum("DebtCredit", "Debt/Credit", 1)
      .withValues(
        "D" -> "Debt"                        -> "Debt",
        "C" -> "Credit"                      -> "Credit",
      ),
  )

}
