package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._

object Finance
    extends FinDataElements
    with Enums
    with Shared
    with Common
    with Ledger
    with GLAccount
    with Analytics
    with Logistic
    with CompanyCode
    with LedgerEntry {

  val financeDomain = domain(
    "Finance",
    "Finance model",
    "Finance model provides tables and data structures similar to SAP ERP Finance."
  )
    .withDataElementSeq(dataElements)
    .withEnumSeq(
      enums
    )
    .withComponents(
      sharedGroup,
      commonGroup,
      ledgerGroup,
      glAccountGroup,
      analyticsGroup,
      logisticGroup,
      companyCodeGroup,
      ledgerEntryGroup
    )

}
