package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._

object Finance
    extends Enums
    with BC
    with CA
    with FI_CC
    with FI_GLS
    with FI_GLA
    with FI_LE
    with FI_BP
    with FI_ANL
    with LO {

  val financeDomain = domain(
    "Finance",
    "Finance model",
    "Finance model provides tables and data structures similar to SAP ERP Finance.",
  )
    .withEnumSeq(
      enums,
    )
    .withComponents(
      bcComponent,
      caComponent,
      component("FI", "Finance")
        .withComponents(
          companyCodeComponent,
          glAccountComponent,
          glSettingsComponent,
          ledgerEntryComponent,
          businessPartnersGroup,
          analyticsComponent,
        ),
      logisticComponent,
    )

}
