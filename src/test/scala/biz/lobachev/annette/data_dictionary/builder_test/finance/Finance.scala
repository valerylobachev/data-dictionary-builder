package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.labels.Audit.audit
import biz.lobachev.annette.data_dictionary.builder.labels.GolangPackage.goModelPackage

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

  val data = domain(
    "Finance",
    "Finance model",
    "Finance model provides tables and data structures similar to SAP ERP Finance.",
  )
    .withLabels(
      goModelPackage("github.com/valerylobachev/annette-go/service/finance/logic/repository/entity"),
      audit("audit")
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
